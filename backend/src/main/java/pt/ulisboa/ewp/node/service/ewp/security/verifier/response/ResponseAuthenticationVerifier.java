package pt.ulisboa.ewp.node.service.ewp.security.verifier.response;

import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.EwpAuthenticationResult;

@Service
public class ResponseAuthenticationVerifier {

  private final Collection<AbstractResponseAuthenticationMethodVerifier> authenticationVerifiers;

  public ResponseAuthenticationVerifier(
      Collection<AbstractResponseAuthenticationMethodVerifier> authenticationVerifiers) {
    this.authenticationVerifiers = authenticationVerifiers;
  }

  public EwpAuthenticationResult verify(EwpRequest request, EwpResponse response) {
    EwpAuthenticationResult firstAuthenticationResult = null;
    boolean firstResult = true;
    for (AbstractResponseAuthenticationMethodVerifier authenticationMethodVerifier :
        this.authenticationVerifiers) {
      if (authenticationMethodVerifier.canBeVerified(request, response)) {
        EwpAuthenticationResult result = authenticationMethodVerifier.verify(request, response);
        if (firstResult) {
          firstAuthenticationResult = result;
          firstResult = false;
        }
        if (result.isValid()) {
          return result;
        }
      }
    }
    if (firstAuthenticationResult == null) {
      throw new IllegalArgumentException(
          "Unknown supported authentication methods: "
              + request.getSupportedServerAuthenticationMethods());
    }
    return firstAuthenticationResult;
  }
}
