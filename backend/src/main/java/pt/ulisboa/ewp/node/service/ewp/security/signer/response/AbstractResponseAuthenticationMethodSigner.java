package pt.ulisboa.ewp.node.service.ewp.security.signer.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface AbstractResponseAuthenticationMethodSigner {

  boolean supports(HttpServletRequest request);

  void sign(HttpServletRequest request, HttpServletResponse response);
}
