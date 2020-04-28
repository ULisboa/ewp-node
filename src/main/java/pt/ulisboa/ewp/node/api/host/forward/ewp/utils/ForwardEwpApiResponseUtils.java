package pt.ulisboa.ewp.node.api.host.forward.ewp.utils;

import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.messaging.Message;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

public class ForwardEwpApiResponseUtils {

  private ForwardEwpApiResponseUtils() {}

  public static <T> ResponseEntity<ForwardEwpApiResponseWithData<T>> toSuccessResponseEntity(
      EwpResponse ewpResponse, T responseBody) {
    ForwardEwpApiResponseWithData<T> response = createResponseWithMessagesAndData(responseBody);
    return ResponseEntity.status(ewpResponse.getStatus())
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  public static <T> ResponseEntity<ForwardEwpApiResponseWithData<T>> toOkResponseEntity(
      T responseBody) {
    ForwardEwpApiResponseWithData<T> response = createResponseWithMessagesAndData(responseBody);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_XML).body(response);
  }

  public static ResponseEntity<ForwardEwpApiResponse> toBadRequestResponseEntity() {
    ForwardEwpApiResponse responseBody = createResponseWithMessages();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_XML)
        .header(HttpConstants.HEADER_X_HAS_DATA_OBJECT, String.valueOf(false))
        .body(responseBody);
  }

  public static ResponseEntity<ForwardEwpApiResponseWithData<ErrorResponse>> toErrorResponseEntity(
      ErrorResponse errorResponse) {
    ForwardEwpApiResponseWithData<ErrorResponse> response =
        createResponseWithMessagesAndData(errorResponse);
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_XML)
        .header(HttpConstants.HEADER_X_HAS_DATA_OBJECT, String.valueOf(true))
        .body(response);
  }

  public static ResponseEntity<ForwardEwpApiResponse> toInternalErrorResponseEntity() {
    ForwardEwpApiResponse response = createResponseWithMessages();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  public static ResponseEntity<ForwardEwpApiResponse> toBadGatewayResponseEntity() {
    ForwardEwpApiResponse response = createResponseWithMessages();
    return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
        .contentType(MediaType.APPLICATION_XML)
        .body(response);
  }

  public static <T> ForwardEwpApiResponseWithData<T> createResponseWithMessagesAndData(T data) {
    ForwardEwpApiResponseWithData<T> response = new ForwardEwpApiResponseWithData<>();
    decorateResponseWithOtherMessages(response);
    response.getData().setObject(data);
    return response;
  }

  public static ForwardEwpApiResponse createResponseWithMessages() {
    ForwardEwpApiResponse response = new ForwardEwpApiResponse();
    decorateResponseWithOtherMessages(response);
    return response;
  }

  public static void decorateResponseWithOtherMessages(ForwardEwpApiResponse response) {
    List<ForwardEwpApiResponse.Message> messagesToAdd =
        MessageService.getInstance().consumeMessages().stream()
            .map(ForwardEwpApiResponseUtils::toMessage)
            .collect(Collectors.toList());
    response.setMessages(messagesToAdd);
  }

  private static ForwardEwpApiResponse.Message toMessage(Message message) {
    ForwardEwpApiResponse.Message result = new ForwardEwpApiResponse.Message();
    result.setContext(message.getContext());
    result.setSeverity(toMessageSeverity(message.getSeverity()));
    result.setSummary(message.getSummary());
    return result;
  }

  private static ForwardEwpApiResponse.Message.MessageSeverity toMessageSeverity(
      Severity severity) {
    switch (severity) {
      case FATAL:
        return ForwardEwpApiResponse.Message.MessageSeverity.FATAL;
      case ERROR:
        return ForwardEwpApiResponse.Message.MessageSeverity.ERROR;
      case WARN:
        return ForwardEwpApiResponse.Message.MessageSeverity.WARN;
      case INFO:
        return ForwardEwpApiResponse.Message.MessageSeverity.INFO;
      default:
        throw new IllegalArgumentException("Unknown severity: " + severity.name());
    }
  }
}
