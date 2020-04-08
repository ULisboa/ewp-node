package pt.ulisboa.ewp.node.api.admin.handler;

import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import pt.ulisboa.ewp.node.api.admin.annotation.AdminApiWithResponseBodyWrapper;
import pt.ulisboa.ewp.node.api.admin.controller.AdminApi;
import pt.ulisboa.ewp.node.api.common.dto.ApiOperationStatusDTO;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.PojoUtils;
import pt.ulisboa.ewp.node.utils.messaging.Message;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

@ControllerAdvice(annotations = AdminApi.class)
@AdminApiWithResponseBodyWrapper
public class AdminApiBadRequestExceptionHandler {

  @Autowired private MessageSource messageSource;

  @ExceptionHandler({MethodArgumentNotValidException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiOperationStatusDTO handleArgumentNotValidException(
      MethodArgumentNotValidException exception) throws NoSuchFieldException {
    MessageService messageService = MessageService.getInstance();
    List<FieldError> fieldErrors = exception.getBindingResult().getFieldErrors();
    Iterator fieldErrorsIterator = fieldErrors.iterator();

    while (fieldErrorsIterator.hasNext()) {
      FieldError fieldError = (FieldError) fieldErrorsIterator.next();
      Message message =
          (new Message.Builder(Severity.ERROR, this.getLocalizedErrorMessage(fieldError)))
              .context(
                  PojoUtils.getUserFriendlyPropertyName(
                      exception.getParameter().getParameterType(), fieldError))
              .build();
      messageService.add(message);
    }

    return new ApiOperationStatusDTO(false);
  }

  @ExceptionHandler({ConstraintViolationException.class})
  @ResponseStatus(HttpStatus.BAD_REQUEST)
  @ResponseBody
  public ApiOperationStatusDTO handleConstraintViolationException(
      ConstraintViolationException exception) {
    MessageService messageService = MessageService.getInstance();
    Set<ConstraintViolation<?>> constraintViolations = exception.getConstraintViolations();
    Iterator constraintViolationsIterator = constraintViolations.iterator();

    while (constraintViolationsIterator.hasNext()) {
      ConstraintViolation<?> constraintViolation =
          (ConstraintViolation) constraintViolationsIterator.next();
      Message message =
          (new Message.Builder(Severity.ERROR, constraintViolation.getMessage()))
              .context(PojoUtils.getUserFriendlyPropertyName(constraintViolation))
              .build();
      messageService.add(message);
    }

    return new ApiOperationStatusDTO(false);
  }

  private String getLocalizedErrorMessage(FieldError fieldError) {
    Locale currentLocale = LocaleContextHolder.getLocale();
    return this.messageSource.getMessage(fieldError, currentLocale);
  }
}
