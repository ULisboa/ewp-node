package pt.ulisboa.ewp.node.service.ewp.iia.hash;

import eu.erasmuswithoutpaper.api.iias.v3.endpoints.IiasGetResponseV3;
import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasGetResponseV4;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6;
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
import pt.ulisboa.ewp.node.exception.ewp.hash.HashCalculationException;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashComparisonException;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;

@Service
public class IiaHashService {

  private final Jaxb2HttpMessageConverter jaxb2HttpMessageConverter;
  private final XPathFactory xpathFactory;

  IiaHashService(Jaxb2HttpMessageConverter jaxb2HttpMessageConverter) {
    this.jaxb2HttpMessageConverter = jaxb2HttpMessageConverter;
    this.xpathFactory = XPathFactory.newInstance();
  }

  /**
   * Calculates the cooperation conditions hash for each interinstitutional agreement V3 provided.
   *
   * @param iias             The interinstitutional agreements to process
   * @param iiasNamespaceUrl The namespace URL of the IIAs Get Response
   * @return A list of hashes for all agreements provided.
   * @throws HashCalculationException when hash failed to be calculated for some reason.
   */
  public List<HashCalculationResult> calculateCooperationConditionsHashesForV3(
      List<IiasGetResponseV3.Iia> iias, String iiasNamespaceUrl) throws HashCalculationException {
    try {
      IiasGetResponseV3 iiasGetResponse = new IiasGetResponseV3();
      iiasGetResponse.getIia().addAll(iias);

      ByteArrayOutputStream iiasGetResponseOutputStream = new ByteArrayOutputStream();
      StreamResult iiasGetResponseStreamResult = new StreamResult(iiasGetResponseOutputStream);
      this.jaxb2HttpMessageConverter.marshal(iiasGetResponse, iiasGetResponseStreamResult);

      return calculateCooperationConditionsHashes(iiasNamespaceUrl,
          iiasGetResponseOutputStream.toByteArray());

    } catch (HashCalculationException e) {
      throw new HashCalculationException(e);
    }
  }

  /**
   * Calculates the cooperation conditions hash for each interinstitutional agreement V4 provided.
   *
   * @param iias             The interinstitutional agreements to process
   * @param iiasNamespaceUrl The namespace URL of the IIAs Get Response
   * @return A list of hashes for all agreements provided.
   * @throws HashCalculationException when hash failed to be calculated for some reason.
   */
  public List<HashCalculationResult> calculateCooperationConditionsHashesForV4(
      List<IiasGetResponseV4.Iia> iias, String iiasNamespaceUrl) throws HashCalculationException {
    try {
      IiasGetResponseV4 iiasGetResponse = new IiasGetResponseV4();
      iiasGetResponse.getIia().addAll(iias);

      ByteArrayOutputStream iiasGetResponseOutputStream = new ByteArrayOutputStream();
      StreamResult iiasGetResponseStreamResult = new StreamResult(iiasGetResponseOutputStream);
      this.jaxb2HttpMessageConverter.marshal(iiasGetResponse, iiasGetResponseStreamResult);

      return calculateCooperationConditionsHashes(iiasNamespaceUrl,
          iiasGetResponseOutputStream.toByteArray());

    } catch (HashCalculationException e) {
      throw new HashCalculationException(e);
    }
  }

  /**
   * Calculates the cooperation conditions hash for each interinstitutional agreement V6 provided.
   *
   * @param iias             The interinstitutional agreements to process
   * @param iiasNamespaceUrl The namespace URL of the IIAs Get Response
   * @return A list of hashes for all agreements provided.
   * @throws HashCalculationException when hash failed to be calculated for some reason.
   */
  public List<HashCalculationResult> calculateCooperationConditionsHashesForV6(
      List<IiasGetResponseV6.Iia> iias,
      String iiasNamespaceUrl) throws HashCalculationException {
    try {
      IiasGetResponseV6 iiasGetResponse = new IiasGetResponseV6();
      iiasGetResponse.getIia().addAll(iias);

      ByteArrayOutputStream iiasGetResponseOutputStream = new ByteArrayOutputStream();
      StreamResult iiasGetResponseStreamResult = new StreamResult(iiasGetResponseOutputStream);
      this.jaxb2HttpMessageConverter.marshal(iiasGetResponse, iiasGetResponseStreamResult);

      return calculateCooperationConditionsHashes(iiasNamespaceUrl,
          iiasGetResponseOutputStream.toByteArray());

    } catch (HashCalculationException e) {
      throw new HashCalculationException(e);
    }
  }

  private List<HashCalculationResult> calculateCooperationConditionsHashes(String iiasNamespaceUrl,
      byte[] iiasGetResponseBytes)
      throws HashCalculationException {
    try {
      XPath xPath = createXPath(iiasNamespaceUrl);
      XPathExpression xpathIiasExpr = xPath.compile("/iia:iias-get-response/iia:iia");

      InputSource iiaXmlInputSource = new InputSource(
          new ByteArrayInputStream(iiasGetResponseBytes));
      Document document = getDocument(iiaXmlInputSource);

      NodeList iiasNodes = (NodeList) xpathIiasExpr.evaluate(document, XPathConstants.NODESET);
      List<HashCalculationResult> hashCalculationResults = new ArrayList<>(iiasNodes.getLength());
      for (int i = 0; i < iiasNodes.getLength(); i++) {
        hashCalculationResults.add(calculateHash(iiasNodes.item(i), iiasNamespaceUrl, xPath));
      }
      return hashCalculationResults;

    } catch (IOException | XPathExpressionException | ElementHashException |
             ParserConfigurationException | SAXException e) {
      throw new HashCalculationException(e);
    }
  }

  /**
   * Checks cooperation conditions hash present in IIA get response.
   *
   * @param iiaXmlInputSource XML containing IIA get response
   * @return list of cooperation conditions hash comparison results (one for every IIA)
   * @throws HashComparisonException when hash comparison failed for some reason
   */
  public List<HashComparisonResult> checkCooperationConditionsHash(InputSource iiaXmlInputSource,
      String iiasNamespaceUrl) throws HashComparisonException {

    try {
      XPath xPath = createXPath(iiasNamespaceUrl);
      XPathExpression xpathIiasExpr = xPath.compile("/iia:iias-get-response/iia:iia");

      Document document = getDocument(iiaXmlInputSource);

      NodeList iias = (NodeList) xpathIiasExpr.evaluate(document, XPathConstants.NODESET);
      List<HashComparisonResult> hashComparisonResults = new ArrayList<>(iias.getLength());
      for (int i = 0; i < iias.getLength(); i++) {
        hashComparisonResults.add(getHashComparisonResult(iias.item(i), iiasNamespaceUrl, xPath));
      }
      return hashComparisonResults;

    } catch (IOException | XPathExpressionException | ElementHashException |
             ParserConfigurationException | SAXException e) {
      throw new HashComparisonException(e);
    }
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
    if (cooperationConditions == null) {
      return;
    }

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
