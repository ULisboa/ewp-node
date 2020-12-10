package pt.ulisboa.ewp.node.client.ewp.operation.result.error;

import eu.erasmuswithoutpaper.api.architecture.v1.ErrorResponseV1;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;

public class EwpErrorResponseOperationResult extends AbstractErrorEwpOperationResult {

  private final ErrorResponseV1 errorResponse;

  protected EwpErrorResponseOperationResult(Builder builder) {
    super(EwpOperationResultErrorType.ERROR_RESPONSE, builder);
    this.errorResponse = builder.errorResponse;
  }

  public ErrorResponseV1 getErrorResponse() {
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

    private ErrorResponseV1 errorResponse;

    public ErrorResponseV1 errorResponse() {
      return errorResponse;
    }

    public Builder errorResponse(ErrorResponseV1 errorResponse) {
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
