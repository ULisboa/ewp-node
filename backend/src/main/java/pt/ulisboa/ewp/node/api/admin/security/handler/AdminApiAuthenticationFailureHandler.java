package pt.ulisboa.ewp.node.api.admin.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiResponseUtils;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

@Component
public class AdminApiAuthenticationFailureHandler implements AuthenticationFailureHandler {

  @Override
  public void onAuthenticationFailure(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException exception)
      throws IOException {
    MessageService.getInstance().add(Severity.ERROR, exception.getMessage());
    AdminApiResponseUtils.writeResponseBody(response, HttpServletResponse.SC_UNAUTHORIZED, null);
  }
}
