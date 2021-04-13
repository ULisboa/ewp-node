package pt.ulisboa.ewp.node.client.ewp.exception;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import eu.erasmuswithoutpaper.api.architecture.v1.MultilineStringV1;
import java.util.stream.Collectors;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;
import pt.ulisboa.ewp.node.service.security.ewp.verifier.EwpAuthenticationResult;

/**
 * Target API returned an error response (see {@see eu.erasmuswithoutpaper.api.architecture.ErrorResponse}).
 */
public class EwpClientErrorResponseException extends EwpClientErrorException {

  private final EwpAuthenticationResult responseAuthenticationResult;
  private final ErrorResponseV1 errorResponse;

  public EwpClientErrorResponseException(
      EwpRequest request, EwpResponse response,
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
    result.append("Error response obtained: ");
    if (getErrorResponse() != null) {
      if (!getErrorResponse().getUserMessage().isEmpty()) {
        result.append(
            getErrorResponse().getUserMessage().stream().map(MultilineStringV1::getValue).collect(
                Collectors.joining(" | ")));
      } else {
        result.append("N/A");
      }

      if (getErrorResponse().getDeveloperMessage() != null) {
        result.append(" [developer message: ");
        result.append(getErrorResponse().getDeveloperMessage().getValue());
        result.append("]");
      }
    }
    return result.toString();
  }
}
