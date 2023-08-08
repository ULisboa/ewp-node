package pt.ulisboa.ewp.node.service.ewp.security.signer.request;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

@Service
public interface AbstractRequestAuthenticationMethodSigner {

  boolean supports(EwpAuthenticationMethod method);

  void sign(EwpRequest request);
}
