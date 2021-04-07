package pt.ulisboa.ewp.node.service.security.ewp.signer.request;

import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;

@Service
public class RequestAuthenticationSigner {

  private final Collection<AbstractRequestAuthenticationMethodSigner> signers;

  public RequestAuthenticationSigner(
      Collection<AbstractRequestAuthenticationMethodSigner> signers) {
    this.signers = signers;
  }

  public void sign(EwpRequest request) {
    signers.stream()
        .filter(s -> s.supports(request.getAuthenticationMethod()))
        .findFirst()
        .ifPresent(s -> s.sign(request));
  }
}
