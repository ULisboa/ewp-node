package pt.ulisboa.ewp.node.client.ewp.operation.result.error;

import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientUnknownErrorResponseException;

public class EwpUnknownErrorResponseOperationResult extends AbstractErrorEwpOperationResult {

  private final String error;

  protected EwpUnknownErrorResponseOperationResult(Builder builder) {
    super(EwpOperationResultErrorType.UNKNOWN_ERROR_RESPONSE, builder);
    this.error = builder.error;
  }

  public String getError() {
    return error;
  }

  @Override
  public AbstractEwpClientErrorException toClientException() {
    return new EwpClientUnknownErrorResponseException(
        getRequest(), getResponse(), getResponseAuthenticationResult(), getError());
  }

  @Override
  public String getSummary() {
    return "Unknown error: " + error.substring(0, 1000);
  }

  public static class Builder extends AbstractErrorEwpOperationResult.Builder<Builder> {

    private String error;

    public String error() {
      return error;
    }

    public Builder error(String error) {
      this.error = error;
      return this;
    }

    @Override
    public Builder getThis() {
      return this;
    }

    public EwpUnknownErrorResponseOperationResult build() {
      return new EwpUnknownErrorResponseOperationResult(this);
    }
  }
}
