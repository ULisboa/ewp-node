package pt.ulisboa.ewp.node.api.host.forward.ewp.handler;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientResponseAuthenticationFailedException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientUnknownErrorResponseException;
import pt.ulisboa.ewp.node.utils.PojoUtils;

@ControllerAdvice(annotations = ForwardEwpApi.class)
public class ForwardEwpApiClientExceptionHandler {

  @Autowired
  private Logger log;

  @Autowired
  private MessageSource messageSource;

  @ExceptionHandler({ConstraintViolationException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleConstraintViolationException(
      ConstraintViolationException exception) {
    Collection<String> errorMessages = new ArrayList<>();
    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
    Iterator<ConstraintViolation<?>> constraintViolationsIterator = constraintViolations.iterator();
    while (constraintViolationsIterator.hasNext()) {
      ConstraintViolation<?> constraintViolation = constraintViolationsIterator.next();
      String errorMessage =
          String.format(
              "%s: %s",
              PojoUtils.getUserFriendlyPropertyName(constraintViolation),
              constraintViolation.getMessage());
      errorMessages.add(errorMessage);
    }

    return ForwardEwpApiResponseUtils.toRequestErrorResponseEntity(errorMessages);
  }

  @ExceptionHandler({EwpClientProcessorException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientProcessorException(
      EwpClientProcessorException exception) {
    return ForwardEwpApiResponseUtils.toProcessorErrorResponseEntity(exception.getMessage());
  }

  @ExceptionHandler({EwpClientResponseAuthenticationFailedException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientResponseAuthenticationFailedException(
      EwpClientResponseAuthenticationFailedException exception) {
    return ForwardEwpApiResponseUtils.toResponseAuthenticationErrorResponseEntity(
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

  @ExceptionHandler({EwpClientUnknownErrorResponseException.class})
  public ResponseEntity<ForwardEwpApiResponse> handleEwpClientUnknownErrorResponseException(
      EwpClientUnknownErrorResponseException exception) {
    return ForwardEwpApiResponseUtils.toUnknownErrorResponseEntity(
        exception.getResponse(), exception.getResponseAuthenticationResult(), exception.getError());
  }

  @ExceptionHandler({Exception.class})
  public ResponseEntity<ForwardEwpApiResponse> handleException(Exception exception) {
    log.warn("Handling exception", exception);
    if (exception.getMessage() == null) {
      return ForwardEwpApiResponseUtils.toProcessorErrorResponseEntity("Internal server error");
    }
    return ForwardEwpApiResponseUtils.toProcessorErrorResponseEntity(exception.getMessage());
  }
}
