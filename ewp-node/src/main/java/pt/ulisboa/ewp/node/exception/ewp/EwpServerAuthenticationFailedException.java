package pt.ulisboa.ewp.node.exception.ewp;

import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

public class EwpServerAuthenticationFailedException extends Exception {

  private EwpRequest request;
  private EwpResponse response;
  private EwpAuthenticationResult authenticationResult;

  public EwpServerAuthenticationFailedException(
      EwpRequest request, EwpResponse response, EwpAuthenticationResult authenticationResult) {
    this.request = request;
    this.response = response;
    this.authenticationResult = authenticationResult;
  }

  public EwpRequest getRequest() {
    return request;
  }

  public EwpResponse getResponse() {
    return response;
  }

  public EwpAuthenticationResult getAuthenticationResult() {
    return authenticationResult;
  }

  @Override
  public String getMessage() {
    return "Server authentication failed: " + authenticationResult.getErrorMessage();
  }
}
