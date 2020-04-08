package pt.ulisboa.ewp.node.service.security.ewp.verifier;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpAuthenticationResult {

  private EwpAuthenticationMethod method;
  private boolean isValid;
  private String errorMessage;

  protected EwpAuthenticationResult(
      EwpAuthenticationMethod method, boolean isValid, String errorMessage) {
    this.method = method;
    this.isValid = isValid;
    this.errorMessage = errorMessage;
  }

  public EwpAuthenticationMethod getMethod() {
    return method;
  }

  public boolean isValid() {
    return isValid;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public static EwpAuthenticationResult createValid(EwpAuthenticationMethod method) {
    return new EwpAuthenticationResult(method, true, null);
  }

  public static EwpAuthenticationResult createInvalid(
      EwpAuthenticationMethod method, String errorMessage) {
    return new EwpAuthenticationResult(method, false, errorMessage);
  }
}
