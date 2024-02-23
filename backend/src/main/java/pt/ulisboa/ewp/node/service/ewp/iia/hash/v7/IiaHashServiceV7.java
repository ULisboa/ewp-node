package pt.ulisboa.ewp.node.service.ewp.iia.hash.v7;

import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashCalculationException;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashComparisonException;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashCalculationResult;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashComparisonResult;
import pt.ulisboa.ewp.node.utils.EwpApiNamespaces;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

/** Class that allows to calculate and validate IIA Hashes V7. */
@Service
public class IiaHashServiceV7 {

  private static final String XSLT_TRANSFORM_VERSION_6_PATH = "iias/v7/transform_version_6.xsl";
  private static final String XSLT_TRANSFORM_VERSION_7_PATH = "iias/v7/transform_version_7.xsl";

  private final Jaxb2HttpMessageConverter jaxb2HttpMessageConverter;
  private final XPathFactory xpathFactory;

  IiaHashServiceV7(Jaxb2HttpMessageConverter jaxb2HttpMessageConverter) {
    this.jaxb2HttpMessageConverter = jaxb2HttpMessageConverter;
    this.xpathFactory = XPathFactory.newInstance();

    System.setProperty(
        "javax.xml.transform.TransformerFactory", "net.sf.saxon.TransformerFactoryImpl");
  }

  /**
   * Calculates the cooperation conditions hash for each interinstitutional agreement V7 provided.
   *
   * @param iias The interinstitutional agreements to process
   * @return A list of hashes for all agreements provided.
   * @throws HashCalculationException when hash failed to be calculated for some reason.
   */
  public List<HashCalculationResult> calculateIiaHashes(List<IiasGetResponseV7.Iia> iias)
      throws HashCalculationException {
    try {
      IiasGetResponseV7 iiasGetResponse = new IiasGetResponseV7();
      iiasGetResponse.getIia().addAll(iias);

      ByteArrayOutputStream iiasGetResponseOutputStream = new ByteArrayOutputStream();
      StreamResult iiasGetResponseStreamResult = new StreamResult(iiasGetResponseOutputStream);
      this.jaxb2HttpMessageConverter.marshal(iiasGetResponse, iiasGetResponseStreamResult);

      return calculateIiaHashes(iiasGetResponseOutputStream.toByteArray(), 7);

    } catch (HashCalculationException e) {
      throw new HashCalculationException(e);
    }
  }

  /**
   * Calculates the cooperation conditions hash for each interinstitutional agreement V7 contained
   * in the XML provided..
   *
   * @param iiasGetResponseBytes A IIAs Get Response as a byte array
   * @return A list of hashes for all agreements contained in the XML provided.
   * @throws HashCalculationException when hash failed to be calculated for some reason.
   */
  public List<HashCalculationResult> calculateIiaHashes(
      byte[] iiasGetResponseBytes, int sourceApiMajorVersion) throws HashCalculationException {
    try {
      byte[] xslt = getXsltForSourceMajorVersion(sourceApiMajorVersion);

      byte[] xmlTransformed = getXmlTransformed(iiasGetResponseBytes, xslt);
      if (XmlUtils.isEmptyXml(xmlTransformed)) {
        return List.of();
      }

      XPath xPath = xpathFactory.newXPath();
      XPathExpression xPathExpression = xPath.compile("/iia/text-to-hash");
      DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
      documentBuilderFactory.setNamespaceAware(true);
      DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
      Document document =
          documentBuilder.parse(new InputSource(new ByteArrayInputStream(xmlTransformed)));
      NodeList textToHashNodeList =
          (NodeList) xPathExpression.evaluate(document, XPathConstants.NODESET);
      if (textToHashNodeList == null) {
        throw new IllegalStateException("Failed to extract IIA hash from calculation");
      }

      List<HashCalculationResult> hashCalculationResults =
          new ArrayList<>(textToHashNodeList.getLength());
      for (int i = 0; i < textToHashNodeList.getLength(); i++) {
        Node textToHashNode = textToHashNodeList.item(i);
        String textToHash = textToHashNode.getTextContent();
        String hash = DigestUtils.sha256Hex(textToHash);
        hashCalculationResults.add(new HashCalculationResult(hash, textToHash));
      }
      return hashCalculationResults;

    } catch (Exception e) {
      throw new HashCalculationException(e);
    }
  }

  /**
   * Checks cooperation conditions hash present in IIA get response.
   *
   * @param iiasGetResponseBytes XML containing IIA get response as a bytearray
   * @return list of cooperation conditions hash comparison results (one for every IIA)
   * @throws HashComparisonException when hash comparison failed for some reason
   */
  public List<HashComparisonResult> checkIiaHashes(byte[] iiasGetResponseBytes)
      throws HashComparisonException {

    try {
      List<HashCalculationResult> hashCalculationResults =
          calculateIiaHashes(iiasGetResponseBytes, 7);

      XPath xPath = createXPath(EwpApiNamespaces.IIAS_V7_GET_RESPONSE.getNamespaceUrl());
      XPathExpression xpathIiaHashesExpr =
          xPath.compile("/iia:iias-get-response/iia:iia/iia:iia-hash");

      Document document = getDocument(iiasGetResponseBytes);

      NodeList iiaHashesNodeList =
          (NodeList) xpathIiaHashesExpr.evaluate(document, XPathConstants.NODESET);
      if (hashCalculationResults.size() != iiaHashesNodeList.getLength()) {
        throw new IllegalStateException(
            "Expected to have "
                + iiaHashesNodeList.getLength()
                + " hashes to check, but got for comparison "
                + hashCalculationResults.size()
                + " actual hashes");
      }

      List<HashComparisonResult> hashComparisonResults =
          new ArrayList<>(iiaHashesNodeList.getLength());
      for (int i = 0; i < iiaHashesNodeList.getLength(); i++) {
        String hashExtracted = iiaHashesNodeList.item(i).getTextContent();
        HashCalculationResult hashCalculationResult = hashCalculationResults.get(i);
        HashComparisonResult hashComparisonResult =
            new HashComparisonResult(
                hashExtracted,
                hashCalculationResult.getHash(),
                hashCalculationResult.getHashedString());
        hashComparisonResults.add(hashComparisonResult);
      }
      return hashComparisonResults;

    } catch (IOException
        | XPathExpressionException
        | ParserConfigurationException
        | SAXException
        | HashCalculationException e) {
      throw new HashComparisonException(e);
    }
  }

  private Document getDocument(byte[] iiasGetResponseBytes)
      throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    return documentBuilder.parse(new InputSource(new ByteArrayInputStream(iiasGetResponseBytes)));
  }

  private XPath createXPath(String iiasNamespaceUrl) {
    XPath xpath = xpathFactory.newXPath();
    xpath.setNamespaceContext(new IiaNamespaceContext(iiasNamespaceUrl));
    return xpath;
  }

  /**
   * Transform an xml file by means of an xslt file
   *
   * @param xmlBytes The content of the xml file
   * @param xsltBytes The content of the xslt file
   * @return An xml useful to compute the hash code
   * @throws Exception
   */
  public byte[] getXmlTransformed(byte[] xmlBytes, byte[] xsltBytes) throws Exception {
    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    dbf.setNamespaceAware(true);

    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(new ByteArrayInputStream(xmlBytes));

    TransformerFactory transformerFactory = TransformerFactory.newInstance();
    Transformer transformer =
        transformerFactory.newTransformer(new StreamSource(new ByteArrayInputStream(xsltBytes)));
    ByteArrayOutputStream output = new ByteArrayOutputStream();

    transformer.transform(new DOMSource(document), new StreamResult(output));

    return output.toByteArray();
  }

  private byte[] getXsltForSourceMajorVersion(int sourceMajorVersion) throws IOException {
    InputStream xsltInputStream;
    if (sourceMajorVersion == 6) {
      xsltInputStream =
          getClass().getClassLoader().getResourceAsStream(XSLT_TRANSFORM_VERSION_6_PATH);
    } else if (sourceMajorVersion == 7) {
      xsltInputStream =
          getClass().getClassLoader().getResourceAsStream(XSLT_TRANSFORM_VERSION_7_PATH);
    } else {
      throw new IllegalArgumentException("Unsupported source major version: " + sourceMajorVersion);
    }

    if (xsltInputStream == null) {
      throw new IllegalStateException(
          "XSLT transformation file was not found for major version: " + sourceMajorVersion);
    }

    byte[] result = xsltInputStream.readAllBytes();
    xsltInputStream.close();
    return result;
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
