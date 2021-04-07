package pt.ulisboa.ewp.node.service.security.ewp.signer.response;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public interface AbstractResponseAuthenticationMethodSigner {

  boolean supports(HttpServletRequest request);

  void sign(HttpServletRequest request, HttpServletResponse response);
}
