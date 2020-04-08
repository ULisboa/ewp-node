package pt.ulisboa.ewp.node.api.common.security.handler;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public abstract class AccessDeniedResponseHandler implements AccessDeniedHandler {

  @Autowired private Logger log;

  @Autowired private MessageResolver messages;

  @Override
  public void handle(
      HttpServletRequest request,
      HttpServletResponse response,
      AccessDeniedException accessDeniedException)
      throws IOException {
    log.warn(
        "[ACCESS DENIED] Blocked access to protected resource: \n{}",
        getRequestRepresentation(request).toString());

    MessageService.getInstance().add(Severity.ERROR, messages.get("auth.error.accessDenied"));

    Object body = getAccessDeniedResponseBody(request, accessDeniedException);

    ObjectMapper mapper = new ObjectMapper();
    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
    response.getWriter().write(mapper.writeValueAsString(body));
  }

  protected abstract Object getAccessDeniedResponseBody(
      HttpServletRequest request, AccessDeniedException accessDeniedException);

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
