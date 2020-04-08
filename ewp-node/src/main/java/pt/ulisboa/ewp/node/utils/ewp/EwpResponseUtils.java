package pt.ulisboa.ewp.node.utils.ewp;

import java.io.StringReader;

import javax.xml.transform.stream.StreamSource;

import org.springframework.oxm.UnmarshallingFailureException;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;

import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.exception.ewp.EwpResponseBodyCannotBeCastToException;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

public class EwpResponseUtils {

  public static <T> T readResponseBody(EwpResponse response, Class<T> classType)
      throws EwpResponseBodyCannotBeCastToException {
    String rawBody = response.getRawBody();

    Jaxb2Marshaller jaxb2Marshaller =
        ApplicationContextProvider.getApplicationContext().getBean(Jaxb2Marshaller.class);

    try {
      Object object = jaxb2Marshaller.unmarshal(new StreamSource(new StringReader(rawBody)));
      if (!classType.isAssignableFrom(object.getClass())) {
        throw new EwpResponseBodyCannotBeCastToException(response, classType);
      }
      return classType.cast(object);
    } catch (UnmarshallingFailureException e) {
      throw new EwpResponseBodyCannotBeCastToException(response, classType);
    }
  }
}
