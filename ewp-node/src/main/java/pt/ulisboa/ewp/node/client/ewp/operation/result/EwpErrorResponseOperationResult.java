package pt.ulisboa.ewp.node.client.ewp.operation.result;

import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;

public class EwpErrorResponseOperationResult extends AbstractEwpOperationResult {

  private ErrorResponse errorResponse;

  protected EwpErrorResponseOperationResult(Builder builder) {
    super(EwpOperationResultType.ERROR_RESPONSE, builder);
    this.errorResponse = builder.errorResponse;
  };

  public ErrorResponse getErrorResponse() {
    return errorResponse;
  }

  public static class Builder extends AbstractEwpOperationResult.Builder<Builder> {

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
