package pt.ulisboa.ewp.node.client.ewp.exception;

import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

public class EwpClientUnknownErrorResponseException extends AbstractEwpClientErrorException {

  private EwpRequest request;
  private EwpResponse response;
  private EwpAuthenticationResult responseAuthenticationResult;
  private String error;

  public EwpClientUnknownErrorResponseException(
      EwpRequest request,
      EwpResponse response,
      EwpAuthenticationResult responseAuthenticationResult,
      String error) {
    this.request = request;
    this.response = response;
    this.responseAuthenticationResult = responseAuthenticationResult;
    this.error = error;
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

  public String getError() {
    return error;
  }

  @Override
  public String getMessage() {
    return "Unknown EWP error response: " + error;
  }
}
