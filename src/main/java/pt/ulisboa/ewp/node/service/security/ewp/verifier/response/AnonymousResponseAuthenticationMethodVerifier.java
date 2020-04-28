package pt.ulisboa.ewp.node.service.security.ewp.verifier.response;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

@Service
public class AnonymousResponseAuthenticationMethodVerifier
    extends AbstractResponseAuthenticationMethodVerifier {

  @Override
  public boolean verifiesAgainstMethod(EwpAuthenticationMethod method) {
    return EwpAuthenticationMethod.ANONYMOUS.equals(method);
  }

  @Override
  public EwpAuthenticationResult verify(EwpRequest request, EwpResponse response) {
    return EwpAuthenticationResult.createValid(EwpAuthenticationMethod.ANONYMOUS);
  }
}
