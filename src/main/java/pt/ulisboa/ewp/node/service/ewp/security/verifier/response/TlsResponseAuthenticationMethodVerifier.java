package pt.ulisboa.ewp.node.service.ewp.security.verifier.response;

import java.net.MalformedURLException;
import java.net.URL;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.EwpAuthenticationResult;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;

@Service
public class TlsResponseAuthenticationMethodVerifier
    implements AbstractResponseAuthenticationMethodVerifier {

  @Override
  public boolean verifiesAgainstMethod(EwpAuthenticationMethod method) {
    return EwpAuthenticationMethod.TLS.equals(method);
  }

  @Override
  public EwpAuthenticationResult verify(EwpRequest request, EwpResponse response) {
    try {
      boolean valid =
          new URL(request.getUrl()).getProtocol().equalsIgnoreCase(HttpConstants.PROTOCOL_HTTPS);
      if (valid) {
        return EwpAuthenticationResult.createValid(EwpAuthenticationMethod.TLS);
      } else {
        return EwpAuthenticationResult.createInvalid(
            EwpAuthenticationMethod.TLS,
            "Request URL is not using " + HttpConstants.PROTOCOL_HTTPS + " protocol");
      }
    } catch (MalformedURLException e) {
      return EwpAuthenticationResult.createInvalid(
          EwpAuthenticationMethod.TLS, "Invalid URL: " + request.getUrl());
    }
  }
}
