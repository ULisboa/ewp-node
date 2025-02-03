package pt.ulisboa.ewp.node.service.ewp.security.signer.response;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;

public class TlsResponseAuthenticationMethodSigner
    implements AbstractResponseAuthenticationMethodSigner {

  @Override
  public boolean supports(HttpServletRequest request) {
    return request.getProtocol().equalsIgnoreCase(HttpConstants.PROTOCOL_HTTPS);
  }

  @Override
  public void sign(HttpServletRequest request, HttpServletResponse response) {}
}
