package pt.ulisboa.ewp.node.api.common.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import pt.ulisboa.ewp.node.api.common.dto.ApiResponseBodyDTO;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

public class ApiUtils {

  private ApiUtils() {}

  public static <T> void writeResponseBody(
      HttpServletResponse response, int statusCode, MediaType mediaType, T data)
      throws IOException, JAXBException {
    response.setStatus(statusCode);
    writeResponseBody(response, mediaType, data);
  }

  public static <T> void writeResponseBody(
      HttpServletResponse response, MediaType mediaType, T data) throws IOException, JAXBException {
    response.addHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString());
    response.getWriter().write(serialize(data, mediaType));
  }

  public static <T> ApiResponseBodyDTO<T> createApiResponseBody(T data) {
    ApiResponseBodyDTO<T> responseBody = new ApiResponseBodyDTO<>();
    responseBody.setMessages(MessageService.getInstance().consumeMessages());
    responseBody.setData(data);
    return responseBody;
  }

  public static String serialize(Object object, MediaType mediaType)
      throws JsonProcessingException, JAXBException {
    if (mediaType.equals(MediaType.APPLICATION_JSON)) {
      return new ObjectMapper().writeValueAsString(object);

    } else if (mediaType.equals(MediaType.APPLICATION_XML)) {
      StringWriter result = new StringWriter();
      ApplicationContextProvider.getApplicationContext()
          .getBean(Jaxb2Marshaller.class)
          .getJaxbContext()
          .createMarshaller()
          .marshal(object, result);
      return result.toString();

    } else {
      throw new IllegalArgumentException("Unsupported media type: " + mediaType.toString());
    }
  }
}
