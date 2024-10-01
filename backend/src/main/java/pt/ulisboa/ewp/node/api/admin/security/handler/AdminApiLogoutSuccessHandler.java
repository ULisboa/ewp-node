package pt.ulisboa.ewp.node.api.admin.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiResponseUtils;

@Component
public class AdminApiLogoutSuccessHandler implements LogoutSuccessHandler {

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response,
        Authentication authentication) throws IOException {
        Map<String, Object> data = new HashMap<>();
        AdminApiResponseUtils.writeResponseBody(response, data);
    }
}
