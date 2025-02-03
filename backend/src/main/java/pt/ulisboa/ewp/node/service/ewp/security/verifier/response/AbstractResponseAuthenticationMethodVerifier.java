package pt.ulisboa.ewp.node.service.ewp.security.verifier.response;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.EwpAuthenticationResult;

@Service
public interface AbstractResponseAuthenticationMethodVerifier {

  boolean canBeVerified(EwpRequest request, EwpResponse response);

  EwpAuthenticationResult verify(EwpRequest request, EwpResponse response);
}
