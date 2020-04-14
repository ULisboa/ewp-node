package pt.ulisboa.ewp.node.client.ewp.operation.result.error;

import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;

public class EwpErrorResponseOperationResult extends AbstractErrorEwpOperationResult {

  private final ErrorResponse errorResponse;

  protected EwpErrorResponseOperationResult(Builder builder) {
    super(EwpOperationResultErrorType.ERROR_RESPONSE, builder);
    this.errorResponse = builder.errorResponse;
  }

  public ErrorResponse getErrorResponse() {
    return errorResponse;
  }

  @Override
  public AbstractEwpClientErrorException toClientException() {
    return new EwpClientErrorResponseException(
        getRequest(), getResponse(), getResponseAuthenticationResult(), getErrorResponse());
  }

  @Override
  public String getSummary() {
    return "Error response obtained";
  }

  public static class Builder extends AbstractErrorEwpOperationResult.Builder<Builder> {

    private ErrorResponse errorResponse;

    public ErrorResponse errorResponse() {
      return errorResponse;
    }

    public Builder errorResponse(ErrorResponse errorResponse) {
      this.errorResponse = errorResponse;
      return this;
    }

    @Override
    public Builder getThis() {
      return this;
    }

    public EwpErrorResponseOperationResult build() {
      return new EwpErrorResponseOperationResult(this);
    }
  }
}
