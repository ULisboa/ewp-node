package pt.ulisboa.ewp.node.service.security.ewp.verifier.request;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.security.ewp.HttpSignatureService;

@Service
public class HttpSignatureRequestAuthenticationMethodVerifier
    implements AbstractRequestAuthenticationMethodVerifier {

  private final HttpSignatureService httpSignatureService;

  public HttpSignatureRequestAuthenticationMethodVerifier(
      HttpSignatureService httpSignatureService) {
    this.httpSignatureService = httpSignatureService;
  }

  @Override
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return EwpAuthenticationMethod.HTTP_SIGNATURE;
  }

  @Override
  public EwpApiAuthenticateMethodResponse verify(EwpApiHttpRequestWrapper request) {
    return httpSignatureService.verifyHttpSignatureRequest(request);
  }
}
