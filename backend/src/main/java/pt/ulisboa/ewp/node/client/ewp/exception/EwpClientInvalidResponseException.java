package pt.ulisboa.ewp.node.client.ewp.exception;

import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.EwpAuthenticationResult;

/**
 * Target API returned an invalid response.
 */
public class EwpClientInvalidResponseException extends EwpClientErrorException {

  private final EwpAuthenticationResult responseAuthenticationResult;
  private final Exception exception;

  public EwpClientInvalidResponseException(
      EwpRequest request,
      EwpResponse response,
      EwpAuthenticationResult responseAuthenticationResult,
      Exception exception) {
    super(request, response);
    this.responseAuthenticationResult = responseAuthenticationResult;
    this.exception = exception;
  }

  public EwpAuthenticationResult getResponseAuthenticationResult() {
    return responseAuthenticationResult;
  }

  public Exception getException() {
    return exception;
  }

  @Override
  public String getMessage() {
    return "Server returned an invalid response: " + exception.getMessage();
  }

  @Override
  public synchronized Throwable getCause() {
    return exception;
  }
}
