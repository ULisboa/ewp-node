package pt.ulisboa.ewp.node.api.admin.security.handler;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiResponseUtils;

@Component
public class AdminApiAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

  @Override
  public void onAuthenticationSuccess(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication)
      throws IOException {
    Map<String, Object> data = new HashMap<>();
    AdminApiResponseUtils.writeResponseBody(response, data);
  }
}
