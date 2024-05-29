package pt.ulisboa.ewp.node.utils.xml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import pt.ulisboa.ewp.node.exception.XmlCannotUnmarshallToTypeException;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

public class XmlUtils {

  private XmlUtils() {}

  /**
   * Converts an object to XML. This XML is optimized. For instance, unused namespaces are removed.
   */
  public static <T> String marshallAndOptimize(T object) {
    try {
      TransformerFactory tf = TransformerFactory.newInstance();
      StreamSource xslt =
          new StreamSource(
              XmlUtils.class
                  .getClassLoader()
                  .getResourceAsStream("xsl/remove-unused-namespaces.xsl"));
      Transformer transformer = tf.newTransformer(xslt);

      String xmlString = marshall(object);

      // NOTE: Marshall object to XML
      ByteArrayOutputStream intermediaryOutputStream = new ByteArrayOutputStream();
      intermediaryOutputStream.write(xmlString.getBytes(StandardCharsets.UTF_8));

      // NOTE: Use XSLT to remove unused namespaces
      ByteArrayInputStream finalInputStream =
          new ByteArrayInputStream(intermediaryOutputStream.toByteArray());
      StringWriter stringWriter = new StringWriter();
      Result result = new StreamResult(stringWriter);
      transformer.transform(new StreamSource(finalInputStream), result);
      return stringWriter.toString();

    } catch (IOException | TransformerException e) {
      throw new RuntimeException(e);
    }
  }

  public static <T> String marshall(T object) {
    Jaxb2Marshaller jaxb2Marshaller =
        ApplicationContextProvider.getApplicationContext().getBean(Jaxb2Marshaller.class);
    return marshall(jaxb2Marshaller, object);
  }

  public static <T> String marshall(Jaxb2Marshaller jaxb2Marshaller, T object) {
    StringWriter stringWriter = new StringWriter();
    Result result = new StreamResult(stringWriter);
    jaxb2Marshaller.marshal(object, result);
    return stringWriter.toString();
  }

  public static <T> T unmarshall(String xml, Class<T> classType)
      throws XmlCannotUnmarshallToTypeException {
    Jaxb2Marshaller jaxb2Marshaller =
        ApplicationContextProvider.getApplicationContext().getBean(Jaxb2Marshaller.class);
    return unmarshall(jaxb2Marshaller, xml, classType);
  }

  public static <T> T unmarshall(Jaxb2Marshaller jaxb2Marshaller, String xml, Class<T> classType)
      throws XmlCannotUnmarshallToTypeException {
    try {
      try {
        return unmarshallInternal(jaxb2Marshaller.createUnmarshaller(), xml, classType);

      } catch (XmlCannotUnmarshallToTypeException e) {
        // NOTE: If the unmarshalling with the normal Unmarshaller fails then try unmarshall
        // by creating a specific JAXBContext for the target class
        JAXBContext jaxbContext = JAXBContext.newInstance(classType);
        return unmarshallInternal(jaxbContext.createUnmarshaller(), xml, classType);
      }

    } catch (UnmarshallingFailureException | JAXBException e) {
      throw new XmlCannotUnmarshallToTypeException(xml, classType);
    }
  }

  private static <T> T unmarshallInternal(Unmarshaller unmarshaller, String xml, Class<T> classType)
      throws XmlCannotUnmarshallToTypeException {
    try {
      JAXBElement<T> jaxbElement =
          unmarshaller.unmarshal(new StreamSource(new StringReader(xml)), classType);
      if (jaxbElement.getValue() == null) {
        throw new XmlCannotUnmarshallToTypeException(xml, classType);
      }

      return jaxbElement.getValue();

    } catch (UnmarshallingFailureException | JAXBException e) {
      throw new XmlCannotUnmarshallToTypeException(xml, classType);
    }
  }

  public static Document deserialize(String xml)
      throws ParserConfigurationException, IOException, SAXException {
    DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
    documentBuilderFactory.setNamespaceAware(true);
    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
    return documentBuilder.parse(new InputSource(new StringReader(xml)));
  }

  public static String serialize(Node node) throws TransformerException {
    StreamResult xmlOutput = new StreamResult(new StringWriter());
    Transformer transformer = TransformerFactory.newInstance().newTransformer();
    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    transformer.transform(new DOMSource(node), xmlOutput);
    return xmlOutput.getWriter().toString();
  }

  public static boolean isEmptyXml(byte[] xml) {
    return isEmptyXml(new String(xml));
  }

  public static boolean isEmptyXml(String xml) {
    String xmlDeclarationPattern = "<\\?xml[\\s\\S]*\\?>";
    String sanitizedXml = xml.replaceAll(xmlDeclarationPattern, "").replaceAll("[\\s]", "");
    return sanitizedXml.isEmpty();
  }
}
