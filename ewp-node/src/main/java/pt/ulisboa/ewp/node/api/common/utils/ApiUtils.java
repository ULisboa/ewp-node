package pt.ulisboa.ewp.node.api.common.utils;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import pt.ulisboa.ewp.node.api.common.dto.ApiResponseBodyDTO;
import pt.ulisboa.ewp.node.service.messaging.MessageService;

import com.fasterxml.jackson.databind.ObjectMapper;

public class ApiUtils {

  public static <T> void writeResponseBody(
      HttpServletResponse response, int statusCode, MediaType mediaType, T data)
      throws IOException {
    response.setStatus(statusCode);
    writeResponseBody(response, mediaType, data);
  }

  public static <T> void writeResponseBody(
      HttpServletResponse response, MediaType mediaType, T data) throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    response.addHeader(HttpHeaders.CONTENT_TYPE, mediaType.toString());
    response.getWriter().write(mapper.writeValueAsString(data));
  }

  public static <T> ApiResponseBodyDTO<T> createApiResponseBody(T data) {
    ApiResponseBodyDTO<T> responseBody = new ApiResponseBodyDTO<>();
    responseBody.setMessages(MessageService.getInstance().consumeMessages());
    responseBody.setData(data);
    return responseBody;
  }
}
