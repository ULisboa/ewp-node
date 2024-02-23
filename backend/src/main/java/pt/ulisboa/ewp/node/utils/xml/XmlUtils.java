package pt.ulisboa.ewp.node.utils.xml;

import java.io.*;
import java.nio.charset.StandardCharsets;
import javax.xml.transform.*;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
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
      Object object = jaxb2Marshaller.unmarshal(new StreamSource(new StringReader(xml)));
      if (!classType.isAssignableFrom(object.getClass())) {
        throw new XmlCannotUnmarshallToTypeException(xml, classType);
      }
      return classType.cast(object);
    } catch (UnmarshallingFailureException e) {
      throw new XmlCannotUnmarshallToTypeException(xml, classType);
    }
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
