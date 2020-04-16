package pt.ulisboa.ewp.node.api.host.forward.ewp.handler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientInvalidResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdException;
import pt.ulisboa.ewp.node.utils.PojoUtils;

@ControllerAdvice(annotations = ForwardEwpApi.class)
public class ForwardEwpApiClientExceptionHandler {

  @Autowired private Logger log;

  @Autowired private MessageSource messageSource;

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleConstraintViolationException(
      ConstraintViolationException exception) {
    List<String> errorMessages = new ArrayList<>();
    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
    Iterator<ConstraintViolation<?>> constraintViolationsIterator = constraintViolations.iterator();
    while (constraintViolationsIterator.hasNext()) {
      ConstraintViolation<?> constraintViolation = constraintViolationsIterator.next();
      String fieldName = PojoUtils.getUserFriendlyPropertyName(constraintViolation);
      String errorMessage = constraintViolation.getMessage();
      errorMessages.add(fieldName + ": " + errorMessage);
    }

    return ForwardEwpApiResponseUtils.toRequestErrorResponseEntity(errorMessages);
  }

  @ExceptionHandler({BindException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleBindException(BindException exception)
      throws NoSuchFieldException {
    List<String> errorMessages = new ArrayList<>();
    for (FieldError fieldError : exception.getFieldErrors()) {
      String fieldName =
          PojoUtils.getUserFriendlyPropertyName(exception.getTarget().getClass(), fieldError);
      String errorMessage = messageSource.getMessage(fieldError, LocaleContextHolder.getLocale());
      errorMessages.add(fieldName + ": " + errorMessage);
    }

    return ForwardEwpApiResponseUtils.toRequestErrorResponseEntity(errorMessages);
  }

  @ExceptionHandler({MissingServletRequestParameterException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleMissingServletRequestParameterException(
      MissingServletRequestParameterException exception) {
    List<String> errorMessages = new ArrayList<>();
    errorMessages.add(exception.getMessage());
    return ForwardEwpApiResponseUtils.toRequestErrorResponseEntity(errorMessages);
  }

  @ExceptionHandler({NoEwpApiForHeiIdException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleNoEwpApiForHeiIdException(
      NoEwpApiForHeiIdException exception) throws NoSuchFieldException {
    List<String> errorMessages = new ArrayList<>();
    errorMessages.add(exception.getMessage());
    return ForwardEwpApiResponseUtils.toRequestErrorResponseEntity(errorMessages);
  }

  @ExceptionHandler({EwpClientProcessorException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientInternalException(
      EwpClientProcessorException exception) {
    return ForwardEwpApiResponseUtils.toInternalErrorResponseEntity(exception.getMessage());
  }

  @ExceptionHandler({EwpClientInvalidResponseException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientInvalidResponseException(
      EwpClientInvalidResponseException exception) {
    return ForwardEwpApiResponseUtils.toInvalidResponseEntity(
        exception.getResponse(), exception.getResponseAuthenticationResult());
  }

  @ExceptionHandler({EwpClientErrorResponseException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientErrorResponseException(
      EwpClientErrorResponseException exception) {
    return ForwardEwpApiResponseUtils.toErrorResponseEntity(
        exception.getResponse(),
        exception.getResponseAuthenticationResult(),
        exception.getErrorResponse());
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ForwardEwpApiResponse> handleException(Exception exception) {
    log.warn("Handling exception", exception);
    if (exception.getMessage() == null) {
      return ForwardEwpApiResponseUtils.toInternalErrorResponseEntity("Internal server error");
    }
    return ForwardEwpApiResponseUtils.toInternalErrorResponseEntity(exception.getMessage());
  }
}
