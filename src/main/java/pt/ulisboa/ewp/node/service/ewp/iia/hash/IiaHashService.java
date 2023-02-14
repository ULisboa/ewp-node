package pt.ulisboa.ewp.node.service.ewp.iia.hash;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.xml.security.Init;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pt.ulisboa.ewp.node.exception.ewp.hash.ElementHashException;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;

@Service
public class IiaHashService {

  private final Jaxb2HttpMessageConverter jaxb2HttpMessageConverter;
  private final XPathFactory xpathFactory;

  IiaHashService(Jaxb2HttpMessageConverter jaxb2HttpMessageConverter)
      throws XPathExpressionException {
    this.jaxb2HttpMessageConverter = jaxb2HttpMessageConverter;
    this.xpathFactory = XPathFactory.newInstance();
  }

  public List<HashCalculationResult> calculateCooperationConditionsHash(List<Iia> iias,
      String iiasNamespaceUrl)
      throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, ElementHashException {
    IiasGetResponseV6 iiasGetResponse = new IiasGetResponseV6();
    iiasGetResponse.getIia().addAll(iias);

    ByteArrayOutputStream iiasGetResponseOutputStream = new ByteArrayOutputStream();
    StreamResult iiasGetResponseStreamResult = new StreamResult(iiasGetResponseOutputStream);
    this.jaxb2HttpMessageConverter.marshal(iiasGetResponse, iiasGetResponseStreamResult);

    XPath xPath = createXPath(iiasNamespaceUrl);
    XPathExpression xpathIiasExpr = xPath.compile("/iia:iias-get-response/iia:iia");

    InputSource iiaXmlInputSource = new InputSource(
        new ByteArrayInputStream(iiasGetResponseOutputStream.toByteArray()));
    Document document = getDocument(iiaXmlInputSource);

    NodeList iiasNodes = (NodeList) xpathIiasExpr.evaluate(document, XPathConstants.NODESET);
    List<HashCalculationResult> hashCalculationResults = new ArrayList<>(iiasNodes.getLength());
    for (int i = 0; i < iiasNodes.getLength(); i++) {
      hashCalculationResults.add(calculateHash(iiasNodes.item(i), iiasNamespaceUrl, xPath));
    }
    return hashCalculationResults;
  }

  /**
   * Checks cooperation conditions hash present in IIA get response.
   *
   * @param iiaXmlInputSource XML containing IIA get response
   * @return list of cooperation conditions hash comparison results (one for every IIA)
   * @throws ElementHashException when hash cannot be calculated
   */
  public List<HashComparisonResult> checkCooperationConditionsHash(InputSource iiaXmlInputSource,
      String iiasNamespaceUrl)
      throws ParserConfigurationException, IOException, SAXException, XPathExpressionException, ElementHashException {

    XPath xPath = createXPath(iiasNamespaceUrl);
    XPathExpression xpathIiasExpr = xPath.compile("/iia:iias-get-response/iia:iia");

    Document document = getDocument(iiaXmlInputSource);

    NodeList iias = (NodeList) xpathIiasExpr.evaluate(document, XPathConstants.NODESET);
    List<HashComparisonResult> hashComparisonResults = new ArrayList<>(iias.getLength());
    for (int i = 0; i < iias.getLength(); i++) {
      hashComparisonResults.add(getHashComparisonResult(iias.item(i), iiasNamespaceUrl, xPath));
    }
    return hashComparisonResults;
  }

  private HashComparisonResult getHashComparisonResult(Node iiaNode, String iiasNamespaceUrl,
      XPath xPath)
      throws XPathExpressionException, ElementHashException {

    XPathExpression xpathCooperationConditionsHashExpr = xPath.compile(
        "iia:conditions-hash/text()");

    String hashExtracted = xpathCooperationConditionsHashExpr.evaluate(iiaNode);
    HashCalculationResult hashCalculationResult = calculateHash(iiaNode, iiasNamespaceUrl, xPath);

    return new HashComparisonResult(hashExtracted, hashCalculationResult.getHash(),
        hashCalculationResult.getHashedString());
  }

  private HashCalculationResult calculateHash(Node iiaNode, String iiasNamespaceUrl, XPath xPath)
      throws XPathExpressionException, ElementHashException {
    XPathExpression xpathCooperationConditionsExpr = xPath.compile("iia:cooperation-conditions");

    Node cooperationConditions = (Node) xpathCooperationConditionsExpr.evaluate(iiaNode,
        XPathConstants.NODE);
    removeContacts(cooperationConditions, iiasNamespaceUrl);
    byte[] dataToHash = getDataToHash(cooperationConditions);
    String hashExpected = DigestUtils.sha256Hex(dataToHash);

    return new HashCalculationResult(hashExpected, new String(dataToHash, StandardCharsets.UTF_8));
  }

  private void removeContacts(Node cooperationConditions, String iiasNamespaceUrl) {
    NodeList mobilitySpecs = cooperationConditions.getChildNodes();
    for (int i = 0; i < mobilitySpecs.getLength(); i++) {
      Node node = mobilitySpecs.item(i);
      if (node.getNodeType() != Node.ELEMENT_NODE) {
        continue;
      }

      Element mobilitySpec = (Element) node;
      NodeList sendingContacts = mobilitySpec.getElementsByTagNameNS(iiasNamespaceUrl,
          "sending-contact");
      for (int j = sendingContacts.getLength() - 1; j >= 0; j--) {
        mobilitySpec.removeChild(sendingContacts.item(j));
      }

      NodeList receivingContacts = mobilitySpec.getElementsByTagNameNS(iiasNamespaceUrl,
          "receiving-contact");
      for (int j = receivingContacts.getLength() - 1; j >= 0; j--) {
        mobilitySpec.removeChild(receivingContacts.item(j));
      }
    }
  }

  private Document getDocument(InputSource iiaXmlInputSource)
      throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    return documentBuilder.parse(iiaXmlInputSource);
  }

  private static byte[] getDataToHash(Node element) throws ElementHashException {
    try {
      Init.init();
      Canonicalizer canonicalizer = Canonicalizer.getInstance(
          Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS);
      ByteArrayOutputStream canonicalWriter = new ByteArrayOutputStream();
      canonicalizer.canonicalizeSubtree(element, canonicalWriter);

      return canonicalWriter.toByteArray();
    } catch (XMLSecurityException cause) {
      throw new ElementHashException(cause);
    }
  }

  private XPath createXPath(String iiasNamespaceUrl) {
    XPath xpath = xpathFactory.newXPath();
    xpath.setNamespaceContext(new IiaNamespaceContext(iiasNamespaceUrl));
    return xpath;
  }

  static class IiaNamespaceContext implements NamespaceContext {

    private final String iiasNamespaceUrl;

    IiaNamespaceContext(String iiasNamespaceUrl) {
      this.iiasNamespaceUrl = iiasNamespaceUrl;
    }

    @Override
    public String getNamespaceURI(String prefix) {
      if ("iia".equals(prefix)) {
        return this.iiasNamespaceUrl;
      }
      throw new IllegalArgumentException(prefix);
    }

    @Override
    public String getPrefix(String namespaceUri) {
      throw new UnsupportedOperationException();
    }

    @Override
    public Iterator<String> getPrefixes(String namespaceUri) {
      throw new UnsupportedOperationException();
    }
  }

}
