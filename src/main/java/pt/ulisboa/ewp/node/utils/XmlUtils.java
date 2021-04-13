package pt.ulisboa.ewp.node.utils;

import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import pt.ulisboa.ewp.node.exception.XmlCannotUnmarshallToTypeException;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

public class XmlUtils {

  private XmlUtils() {
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
}
