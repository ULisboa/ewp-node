package pt.ulisboa.ewp.node.client.ewp.exception;

import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

public class EwpClientResponseAuthenticationFailedException
    extends AbstractEwpClientErrorException {

  private EwpRequest request;
  private EwpResponse response;
  private EwpAuthenticationResult responseAuthenticationResult;

  public EwpClientResponseAuthenticationFailedException(
      EwpRequest request,
      EwpResponse response,
      EwpAuthenticationResult responseAuthenticationResult) {
    this.request = request;
    this.response = response;
    this.responseAuthenticationResult = responseAuthenticationResult;
  }

  public EwpRequest getRequest() {
    return request;
  }

  public EwpResponse getResponse() {
    return response;
  }

  public EwpAuthenticationResult getResponseAuthenticationResult() {
    return responseAuthenticationResult;
  }

  @Override
  public String getMessage() {
    return "EWP response failed security verification for method "
        + responseAuthenticationResult.getMethod()
        + ": "
        + responseAuthenticationResult.getErrorMessage();
  }
}
