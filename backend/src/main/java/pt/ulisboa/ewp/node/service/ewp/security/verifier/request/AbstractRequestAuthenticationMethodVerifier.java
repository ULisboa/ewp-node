package pt.ulisboa.ewp.node.service.ewp.security.verifier.request;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

@Service
public interface AbstractRequestAuthenticationMethodVerifier {

  EwpAuthenticationMethod getAuthenticationMethod();

  EwpApiAuthenticateMethodResponse verify(EwpApiHttpRequestWrapper request);
}
