package pt.ulisboa.ewp.node.client.ewp.exception;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

/**
 * Request failed due to a conflict.
 */
public class EwpClientConflictException extends EwpClientErrorException {

  private final EwpAuthenticationResult responseAuthenticationResult;
  private final ErrorResponseV1 errorResponse;

  public EwpClientConflictException(EwpRequest request, EwpResponse response,
      EwpAuthenticationResult responseAuthenticationResult,
      ErrorResponseV1 errorResponse) {
    super(request, response);
    this.responseAuthenticationResult = responseAuthenticationResult;
    this.errorResponse = errorResponse;
  }

  public EwpAuthenticationResult getResponseAuthenticationResult() {
    return responseAuthenticationResult;
  }

  public ErrorResponseV1 getErrorResponse() {
    return errorResponse;
  }

  @Override
  public String getMessage() {
    StringBuilder result = new StringBuilder();
    result.append("Conflict: ");
    if (errorResponse.getUserMessage() != null && !errorResponse.getUserMessage().isEmpty()) {
      result.append(errorResponse.getUserMessage().get(0).getValue());
    }
    return result.toString();
  }
}
