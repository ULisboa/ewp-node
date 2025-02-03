package pt.ulisboa.ewp.node.service.ewp.security.verifier.response;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.EwpAuthenticationResult;

@Service
public class AnonymousResponseAuthenticationMethodVerifier
    implements AbstractResponseAuthenticationMethodVerifier {

  @Override
  public boolean canBeVerified(EwpRequest request, EwpResponse response) {
    return request
        .getSupportedServerAuthenticationMethods()
        .contains(EwpAuthenticationMethod.ANONYMOUS);
  }

  @Override
  public EwpAuthenticationResult verify(EwpRequest request, EwpResponse response) {
    return EwpAuthenticationResult.createValid(EwpAuthenticationMethod.ANONYMOUS);
  }
}
