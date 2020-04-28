package pt.ulisboa.ewp.node.service.security.ewp.verifier.response;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.security.ewp.HttpSignatureService;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

@Service
public class HttpSignatureResponseAuthenticationMethodVerifier
    extends AbstractResponseAuthenticationMethodVerifier {

  private HttpSignatureService httpSignatureService;

  public HttpSignatureResponseAuthenticationMethodVerifier(
      HttpSignatureService httpSignatureService) {
    this.httpSignatureService = httpSignatureService;
  }

  @Override
  public boolean verifiesAgainstMethod(EwpAuthenticationMethod method) {
    return EwpAuthenticationMethod.HTTP_SIGNATURE.equals(method);
  }

  @Override
  public EwpAuthenticationResult verify(EwpRequest request, EwpResponse response) {
    return httpSignatureService.verifyHttpSignatureResponse(
        request.getMethod().name(),
        request.getUrlWithoutQueryParams(),
        response.getHeaders(),
        response.getRawBody(),
        request.getId());
  }
}
