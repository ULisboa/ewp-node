package pt.ulisboa.ewp.node.api.admin.handler;

import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import pt.ulisboa.ewp.node.api.admin.annotation.AdminApiWithResponseBodyWrapper;
import pt.ulisboa.ewp.node.api.admin.controller.AdminApi;
import pt.ulisboa.ewp.node.api.common.dto.ApiOperationStatusDTO;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

@ControllerAdvice(annotations = AdminApi.class)
@AdminApiWithResponseBodyWrapper
public class AdminApiAccessDeniedExceptionHandler {

  @Autowired private Logger log;

  @Autowired private MessageResolver messages;

  @ExceptionHandler({AccessDeniedException.class})
  @ResponseStatus(HttpStatus.FORBIDDEN)
  @ResponseBody
  public ApiOperationStatusDTO handleException(HttpServletRequest request) {
    log.warn(
        "[ACCESS DENIED] Blocked access to protected resource: \n{}",
        this.getRequestRepresentation(request));
    MessageService.getInstance().add(Severity.ERROR, this.messages.get("error.auth.accessDenied"));

    return new ApiOperationStatusDTO(false);
  }

  private StringBuilder getRequestRepresentation(HttpServletRequest request) {
    StringBuilder requestRepresentation = new StringBuilder();
    requestRepresentation
        .append("URI: ")
        .append(request.getRequestURI())
        .append(System.lineSeparator());
    Authentication authentication = (Authentication) request.getUserPrincipal();
    requestRepresentation
        .append("User: ")
        .append(authentication.getName())
        .append(" ")
        .append(authentication.getAuthorities());
    return requestRepresentation;
  }
}
