package pt.ulisboa.ewp.node.service.ewp.iia;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.xml.security.c14n.CanonicalizationException;
import org.apache.xml.security.c14n.Canonicalizer;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

@Service
public class ConditionsHashCalculator {

  public ConditionsHashCalculator() {
    org.apache.xml.security.Init.init();
  }

  public String calculateHashFor(String cooperationConditionsXml) {
    Node cooperationConditionsNode = getCooperationConditionsNodeFromXml(cooperationConditionsXml);
    removeNodesByExpression(cooperationConditionsNode, "//sending-contact | //receiving-contact");
    String canonizedXml = canonizeXml(cooperationConditionsNode);
    return DigestUtils.sha256Hex(canonizedXml);
  }

  private Node getCooperationConditionsNodeFromXml(String xml) {
    DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
    try {
      DocumentBuilder builder = builderFactory.newDocumentBuilder();

      Document xmlDocument;
      InputSource is = new InputSource(new StringReader(xml));
      xmlDocument = builder.parse(is);

      XPath xPath = XPathFactory.newInstance().newXPath();
      String expression = "//cooperation-conditions";

      return (Node) xPath.compile(expression).evaluate(xmlDocument, XPathConstants.NODE);

    } catch (ParserConfigurationException | IOException | SAXException | XPathExpressionException e) {
      throw new IllegalStateException(e);
    }
  }

  private void removeNodesByExpression(Node node, String expression) {

    try {
      XPath xPath = XPathFactory.newInstance().newXPath();
      NodeList nodesToRemove = (NodeList) xPath.compile(expression)
          .evaluate(node, XPathConstants.NODESET);

      for (int nodeToRemoveIndex = 0; nodeToRemoveIndex < nodesToRemove.getLength();
          nodeToRemoveIndex++) {
        Node nodeToRemove = nodesToRemove.item(nodeToRemoveIndex);
        nodeToRemove.getParentNode().removeChild(nodeToRemove);
      }

    } catch (XPathExpressionException e) {
      throw new IllegalStateException(e);
    }
  }

  private String canonizeXml(Node node) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      Canonicalizer.getInstance(Canonicalizer.ALGO_ID_C14N_EXCL_OMIT_COMMENTS)
          .canonicalizeSubtree(node, byteArrayOutputStream);
      return byteArrayOutputStream.toString();

    } catch (CanonicalizationException | InvalidCanonicalizerException e) {
      throw new IllegalStateException(e);
    }
  }

}
