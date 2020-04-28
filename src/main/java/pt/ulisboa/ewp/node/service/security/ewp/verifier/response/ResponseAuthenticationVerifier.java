package pt.ulisboa.ewp.node.service.security.ewp.verifier.response;

import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

@Service
public class ResponseAuthenticationVerifier {

  private Collection<AbstractResponseAuthenticationMethodVerifier> authenticationVerifiers;

  public ResponseAuthenticationVerifier(
      Collection<AbstractResponseAuthenticationMethodVerifier> authenticationVerifiers) {
    this.authenticationVerifiers = authenticationVerifiers;
  }

  public EwpAuthenticationResult verifyAgainstMethod(EwpRequest request, EwpResponse response) {
    return authenticationVerifiers.stream()
        .filter(v -> v.verifiesAgainstMethod(request.getAuthenticationMethod()))
        .findFirst()
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Unknown authentication method: " + request.getAuthenticationMethod()))
        .verify(request, response);
  }
}
