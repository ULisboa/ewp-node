package pt.ulisboa.ewp.node.utils;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.springframework.stereotype.Service;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

@Service
public class XmlValidator {

  public boolean validateXpath(String xml, String xpath, URL schemaUrl)
      throws IOException, SAXException, ParserConfigurationException, XPathExpressionException {
    Document document = XmlUtil.deserialize(xml);
    Node node =
        ((NodeList)
                XPathFactory.newInstance()
                    .newXPath()
                    .compile(xpath)
                    .evaluate(document, XPathConstants.NODESET))
            .item(0);
    try {
      return validate(XmlUtil.serialize(node), schemaUrl);
    } catch (TransformerException e) {
      e.printStackTrace();
      return false;
    }
  }

  public boolean validate(String xml, URL schemaUrl) {
    try {
      SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
      Schema schema = schemaFactory.newSchema(schemaUrl);
      Validator validator = schema.newValidator();
      validator.validate(new StreamSource(new StringReader(xml)));
      return true;
    } catch (IOException | SAXException e) {
      e.printStackTrace();
      return false;
    }
  }
}
