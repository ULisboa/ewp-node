package pt.ulisboa.ewp.node.client.ewp.exception;

import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

public class EwpClientErrorResponseException extends AbstractEwpClientErrorException {

  private EwpRequest request;
  private EwpResponse response;
  private EwpAuthenticationResult responseAuthenticationResult;
  private ErrorResponse errorResponse;

  public EwpClientErrorResponseException(
      EwpRequest request,
      EwpResponse response,
      EwpAuthenticationResult responseAuthenticationResult,
      ErrorResponse errorResponse) {
    this.request = request;
    this.response = response;
    this.responseAuthenticationResult = responseAuthenticationResult;
    this.errorResponse = errorResponse;
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

  public ErrorResponse getErrorResponse() {
    return errorResponse;
  }

  @Override
  public String getMessage() {
    return "Error response obtained: " + response.getRawBody();
  }
}
