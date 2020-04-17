package pt.ulisboa.ewp.node.utils;

import java.io.StringReader;
import javax.xml.transform.stream.StreamSource;
import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import pt.ulisboa.ewp.node.exception.XmlCannotUnmarshallToTypeException;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

public class XmlUtils {

  public static <T> T unmarshall(String xml, Class<T> classType)
      throws XmlCannotUnmarshallToTypeException {
    Jaxb2Marshaller jaxb2Marshaller =
        ApplicationContextProvider.getApplicationContext().getBean(Jaxb2Marshaller.class);

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
