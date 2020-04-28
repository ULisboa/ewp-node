package pt.ulisboa.ewp.node.api.host.forward.ewp.handler;

import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientInvalidResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdException;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.PojoUtils;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

@ControllerAdvice(annotations = ForwardEwpApi.class)
public class ForwardEwpApiClientExceptionHandler {

  @Autowired private Logger log;

  @Autowired private MessageResolver messages;

  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ForwardEwpApiResponse> handleConstraintViolationException(
      ConstraintViolationException exception) {
    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
    for (ConstraintViolation<?> constraintViolation : constraintViolations) {
      String fieldName = PojoUtils.getUserFriendlyPropertyName(constraintViolation);
      String errorMessage = constraintViolation.getMessage();
      MessageService.getInstance().add(fieldName, Severity.ERROR, errorMessage);
    }

    return ForwardEwpApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({BindException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ForwardEwpApiResponse> handleBindException(BindException exception)
      throws NoSuchFieldException {
    for (FieldError fieldError : exception.getFieldErrors()) {
      String fieldName =
          PojoUtils.getUserFriendlyPropertyName(exception.getTarget().getClass(), fieldError);
      String errorMessage = messages.get(fieldError);
      MessageService.getInstance().add(fieldName, Severity.ERROR, errorMessage);
    }

    return ForwardEwpApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({MissingServletRequestParameterException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ForwardEwpApiResponse> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception) {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    return ForwardEwpApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({NoEwpApiForHeiIdException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ForwardEwpApiResponse> handleNoEwpApiForHeiIdException(
      NoEwpApiForHeiIdException exception) {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    return ForwardEwpApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({EwpClientProcessorException.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientInternalException(
      EwpClientProcessorException exception) {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    return ForwardEwpApiResponseUtils.toInternalErrorResponseEntity();
  }

  @ExceptionHandler({EwpClientInvalidResponseException.class})
  @ResponseStatus(HttpStatus.BAD_GATEWAY)
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientInvalidResponseException(
      EwpClientInvalidResponseException exception) {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    return ForwardEwpApiResponseUtils.toBadGatewayResponseEntity();
  }

  @ExceptionHandler({EwpClientErrorResponseException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  public ResponseEntity<ForwardEwpApiResponseWithData<ErrorResponse>>
      handleEwpClientErrorResponseException(EwpClientErrorResponseException exception) {
    return ForwardEwpApiResponseUtils.toErrorResponseEntity(exception.getErrorResponse());
  }

  @ExceptionHandler({AccessDeniedException.class})
  @ResponseStatus(HttpStatus.FORBIDDEN)
  public ResponseEntity<ForwardEwpApiResponse> handleAccessDeniedException(
      AccessDeniedException exception) {
    log.warn("Blocked access to protected resource");
    MessageService.getInstance().add(Severity.ERROR, messages.get("error.auth.accessDenied"));
    return ResponseEntity.status(HttpStatus.FORBIDDEN)
        .body(ForwardEwpApiResponseUtils.createResponseWithMessages());
  }

  @ExceptionHandler({Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  public ResponseEntity<ForwardEwpApiResponse> handleException(Exception exception) {
    log.error("Wrapping exception", exception);
    if (exception.getMessage() == null) {
      MessageService.getInstance().add(Severity.ERROR, "Internal server error");
    } else {
      MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    }
    return ForwardEwpApiResponseUtils.toInternalErrorResponseEntity();
  }
}
