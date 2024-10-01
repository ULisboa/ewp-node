package pt.ulisboa.ewp.node.api.admin.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
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
    data.put("username", authentication.getName());
    AdminApiResponseUtils.writeResponseBody(response, data);
  }
}
