package pt.ulisboa.ewp.node.client.ewp.exception;

import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

public class EwpClientInvalidResponseException extends AbstractEwpClientErrorException {

  private EwpRequest request;
  private EwpResponse response;
  private EwpAuthenticationResult responseAuthenticationResult;
  private Exception exception;

  public EwpClientInvalidResponseException(
      EwpRequest request,
      EwpResponse response,
      EwpAuthenticationResult responseAuthenticationResult,
      Exception exception) {
    this.request = request;
    this.response = response;
    this.responseAuthenticationResult = responseAuthenticationResult;
    this.exception = exception;
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

  public Exception getException() {
    return exception;
  }

  @Override
  public String getMessage() {
    return "Invalid server response: " + exception.getMessage();
  }
}
