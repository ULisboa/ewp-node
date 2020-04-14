package pt.ulisboa.ewp.node.service.security.ewp.verifier.response;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

@Service
public abstract class AbstractResponseAuthenticationMethodVerifier {

  public abstract boolean verifiesAgainstMethod(EwpAuthenticationMethod method);

  public abstract EwpAuthenticationResult verify(EwpRequest request, EwpResponse response);
}
