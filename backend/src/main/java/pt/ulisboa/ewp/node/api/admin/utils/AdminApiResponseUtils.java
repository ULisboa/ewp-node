package pt.ulisboa.ewp.node.api.admin.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import pt.ulisboa.ewp.node.api.admin.dto.response.AdminApiResponseDto;
import pt.ulisboa.ewp.node.api.admin.dto.response.AdminApiResponseWithDataDto;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContext;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.messaging.Message;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

public class AdminApiResponseUtils {

  private AdminApiResponseUtils() {}

  public static <T> void writeResponseBody(HttpServletResponse response, int statusCode, T data)
      throws IOException {
    response.setStatus(statusCode);
    writeResponseBody(response, data);
  }

  public static <T> void writeResponseBody(HttpServletResponse response, T data)
      throws IOException {
    ObjectMapper mapper = new ObjectMapper();
    AdminApiResponseWithDataDto<Object> responseBody = createResponseWithMessagesAndData(data);
    response.addHeader("Content-Type", MediaType.APPLICATION_JSON.toString());
    response.getWriter().write(mapper.writeValueAsString(responseBody));
  }

  public static <T> ResponseEntity<AdminApiResponseWithDataDto<T>> toOkResponseEntity(
      T responseBody) {
    AdminApiResponseWithDataDto<T> response = createResponseWithMessagesAndData(responseBody);
    return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(response);
  }

  public static ResponseEntity<AdminApiResponseDto> toBadRequestResponseEntity() {
    AdminApiResponseDto responseBody = createResponseWithMessages();
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .contentType(MediaType.APPLICATION_JSON)
        .body(responseBody);
  }

  public static ResponseEntity<AdminApiResponseDto> toInternalErrorResponseEntity() {
    AdminApiResponseDto response = createResponseWithMessages();
    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
        .contentType(MediaType.APPLICATION_JSON)
        .body(response);
  }

  public static <T> AdminApiResponseWithDataDto<T> createResponseWithMessagesAndData(T data) {
    AdminApiResponseWithDataDto<T> response = new AdminApiResponseWithDataDto<>();
    decorateResponseWithOtherMessages(response);
    response.setData(data);
    return response;
  }

  public static AdminApiResponseDto createResponseWithMessages() {
    AdminApiResponseDto response = new AdminApiResponseDto();
    decorateResponseWithOtherMessages(response);
    return response;
  }

  private static Long getCurrentCommunicationId() {
    CommunicationContext context = CommunicationContextHolder.getContext();
    if (context.getCurrentCommunicationLog() == null) {
      return null;
    }
    return context.getCurrentCommunicationLog().getId();
  }

  public static void decorateResponseWithOtherMessages(AdminApiResponseDto response) {
    List<AdminApiResponseDto.Message> messagesToAdd =
        MessageService.getInstance().consumeMessages().stream()
            .map(AdminApiResponseUtils::toMessage)
            .collect(Collectors.toList());
    response.setMessages(messagesToAdd);
  }

  private static AdminApiResponseDto.Message toMessage(Message message) {
    AdminApiResponseDto.Message result = new AdminApiResponseDto.Message();
    result.setContext(message.getContext());
    result.setSeverity(toMessageSeverity(message.getSeverity()));
    result.setSummary(message.getSummary());
    return result;
  }

  private static AdminApiResponseDto.Message.MessageSeverity toMessageSeverity(Severity severity) {
    switch (severity) {
      case FATAL:
        return AdminApiResponseDto.Message.MessageSeverity.FATAL;
      case ERROR:
        return AdminApiResponseDto.Message.MessageSeverity.ERROR;
      case WARN:
        return AdminApiResponseDto.Message.MessageSeverity.WARN;
      case INFO:
        return AdminApiResponseDto.Message.MessageSeverity.INFO;
      default:
        throw new IllegalArgumentException("Unknown severity: " + severity.name());
    }
  }
}
