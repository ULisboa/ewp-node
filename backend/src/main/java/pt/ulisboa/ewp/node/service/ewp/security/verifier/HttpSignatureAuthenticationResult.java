package pt.ulisboa.ewp.node.service.ewp.security.verifier;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class HttpSignatureAuthenticationResult extends EwpAuthenticationResult {

  protected HttpSignatureAuthenticationResult(boolean isValid, String errorMessage) {
    super(EwpAuthenticationMethod.HTTP_SIGNATURE, isValid, errorMessage);
  }

  public static HttpSignatureAuthenticationResult createValid() {
    return new HttpSignatureAuthenticationResult(true, null);
  }

  public static HttpSignatureAuthenticationResult createInvalid(String errorMessage) {
    return new HttpSignatureAuthenticationResult(false, errorMessage);
  }
}
