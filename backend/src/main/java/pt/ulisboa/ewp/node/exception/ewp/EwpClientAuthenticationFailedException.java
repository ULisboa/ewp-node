package pt.ulisboa.ewp.node.exception.ewp;

import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;

public class EwpClientAuthenticationFailedException extends Exception {

  private final EwpRequest request;
  private final EwpResponse response;
  private final String errorMessage;

  public EwpClientAuthenticationFailedException(
      EwpRequest request, EwpResponse response, String errorMessage) {
    this.request = request;
    this.response = response;
    this.errorMessage = errorMessage;
  }

  public EwpRequest getRequest() {
    return request;
  }

  public EwpResponse getResponse() {
    return response;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  @Override
  public String getMessage() {
    return "Client authentication failed for authentication method "
        + request.getClientAuthenticationMethod()
        + ": "
        + errorMessage;
  }
}
