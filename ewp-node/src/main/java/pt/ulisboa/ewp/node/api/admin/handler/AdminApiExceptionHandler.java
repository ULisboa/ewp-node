package pt.ulisboa.ewp.node.api.admin.handler;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import pt.ulisboa.ewp.node.api.admin.annotation.AdminApiWithResponseBodyWrapper;
import pt.ulisboa.ewp.node.api.admin.controller.AdminApi;
import pt.ulisboa.ewp.node.api.common.dto.ApiOperationStatusDTO;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

@ControllerAdvice(annotations = {AdminApi.class})
@AdminApiWithResponseBodyWrapper
public class AdminApiExceptionHandler extends ResponseEntityExceptionHandler {

  @Autowired private Logger log;

  @Autowired private MessageResolver messages;

  @ExceptionHandler(value = {Exception.class})
  @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
  @ResponseBody
  public ApiOperationStatusDTO handleGenericException(Exception exception) {
    log.error("Exception found: ", exception);

    if (!MessageService.getInstance().hasMessages()) {
      MessageService.getInstance().add(Severity.ERROR, messages.get("error.unknown.msg"));
    }

    return new ApiOperationStatusDTO(false);
  }

  @Override
  protected ResponseEntity<Object> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return ResponseEntity.badRequest().body(new ApiOperationStatusDTO(false));
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestPart(
      MissingServletRequestPartException exception,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return ResponseEntity.badRequest().body(new ApiOperationStatusDTO(false));
  }

  @Override
  protected ResponseEntity<Object> handleMissingServletRequestParameter(
      MissingServletRequestParameterException exception,
      HttpHeaders headers,
      HttpStatus status,
      WebRequest request) {
    return ResponseEntity.badRequest().body(new ApiOperationStatusDTO(false));
  }
}
