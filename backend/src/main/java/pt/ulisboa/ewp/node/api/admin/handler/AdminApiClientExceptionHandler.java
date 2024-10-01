package pt.ulisboa.ewp.node.api.admin.handler;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pt.ulisboa.ewp.node.api.admin.controller.AdminApi;
import pt.ulisboa.ewp.node.api.admin.dto.response.AdminApiResponseDto;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiResponseUtils;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.PojoUtils;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

@ControllerAdvice(annotations = AdminApi.class)
public class AdminApiClientExceptionHandler {

  private static final Logger LOG = LoggerFactory.getLogger(AdminApiClientExceptionHandler.class);
  private final MessageResolver messages;

  public AdminApiClientExceptionHandler(MessageResolver messages) {
    this.messages = messages;
  }

  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<AdminApiResponseDto> handleConstraintViolationException(
      ConstraintViolationException exception) {
    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
    for (ConstraintViolation<?> constraintViolation : constraintViolations) {
      String fieldName = PojoUtils.getUserFriendlyPropertyName(constraintViolation);
      String errorMessage = constraintViolation.getMessage();
      MessageService.getInstance().add(fieldName, Severity.ERROR, errorMessage);
    }

    return AdminApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({BindException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<AdminApiResponseDto> handleBindException(BindException exception) {
    Object target = exception.getTarget();
    if (target != null) {
      for (FieldError fieldError : exception.getFieldErrors()) {
        String fieldName = PojoUtils.getUserFriendlyPropertyName(target.getClass(), fieldError);
        String errorMessage = messages.get(fieldError);
        MessageService.getInstance().add(fieldName, Severity.ERROR, errorMessage);
      }
    }

    return AdminApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({MissingServletRequestParameterException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<AdminApiResponseDto> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception) {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    return AdminApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({AccessDeniedException.class})
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<AdminApiResponseDto> handleAccessDeniedException(
      AccessDeniedException exception) {
    LOG.warn("Blocked access to protected resource");
    MessageService.getInstance().add(Severity.ERROR, messages.get("error.auth.accessDenied"));
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(AdminApiResponseUtils.createResponseWithMessages());
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<AdminApiResponseDto> handleException(Exception exception) {
    LOG.error("Wrapping exception", exception);
    if (exception.getMessage() == null) {
      MessageService.getInstance().add(Severity.ERROR, "Internal server error");
    } else {
      MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    }

    return AdminApiResponseUtils.toInternalErrorResponseEntity();
  }
}
