package pt.ulisboa.ewp.node.api.host.forward.ewp.handler;

import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
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
import pt.ulisboa.ewp.node.utils.messaging.Severity;

@ControllerAdvice(annotations = ForwardEwpApi.class)
public class ForwardEwpApiClientExceptionHandler {

  @Autowired private Logger log;

  @Autowired private MessageSource messageSource;

  @ExceptionHandler({ConstraintViolationException.class})
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
  public ResponseEntity<ForwardEwpApiResponse> handleBindException(BindException exception)
      throws NoSuchFieldException {
    for (FieldError fieldError : exception.getFieldErrors()) {
      String fieldName =
          PojoUtils.getUserFriendlyPropertyName(exception.getTarget().getClass(), fieldError);
      String errorMessage = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
      MessageService.getInstance().add(fieldName, Severity.ERROR, errorMessage);
    }

    return ForwardEwpApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({MissingServletRequestParameterException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception) {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    return ForwardEwpApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({NoEwpApiForHeiIdException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleNoEwpApiForHeiIdException(
      NoEwpApiForHeiIdException exception) {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    return ForwardEwpApiResponseUtils.toBadRequestResponseEntity();
  }

  @ExceptionHandler({EwpClientProcessorException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientInternalException(
      EwpClientProcessorException exception) {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    return ForwardEwpApiResponseUtils.toInternalErrorResponseEntity();
  }

  @ExceptionHandler({EwpClientInvalidResponseException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientInvalidResponseException(
      EwpClientInvalidResponseException exception) {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    return ForwardEwpApiResponseUtils.toBadGatewayResponseEntity();
  }

  @ExceptionHandler({EwpClientErrorResponseException.class})
  public ResponseEntity<ForwardEwpApiResponseWithData<ErrorResponse>>
      handleEwpClientErrorResponseException(EwpClientErrorResponseException exception) {
    return ForwardEwpApiResponseUtils.toErrorResponseEntity(
        exception.getResponse(), exception.getErrorResponse());
  }

  @ExceptionHandler({Exception.class})
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
