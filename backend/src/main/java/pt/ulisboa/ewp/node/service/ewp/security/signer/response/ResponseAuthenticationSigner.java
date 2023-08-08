package pt.ulisboa.ewp.node.service.ewp.security.signer.response;

import java.util.Collection;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Service;

@Service
public class ResponseAuthenticationSigner {

  private final Collection<AbstractResponseAuthenticationMethodSigner> signers;

  public ResponseAuthenticationSigner(
      Collection<AbstractResponseAuthenticationMethodSigner> signers) {
    this.signers = signers;
  }

  public void sign(HttpServletRequest request, HttpServletResponse response) {
    signers.stream()
        .filter(s -> s.supports(request))
        .findFirst()
        .ifPresent(s -> s.sign(request, response));
  }
}
