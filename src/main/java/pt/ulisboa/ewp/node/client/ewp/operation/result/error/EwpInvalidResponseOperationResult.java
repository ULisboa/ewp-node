package pt.ulisboa.ewp.node.client.ewp.operation.result.error;

import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientInvalidResponseException;

public class EwpInvalidResponseOperationResult extends AbstractErrorEwpOperationResult {

  private final Exception exception;

  protected EwpInvalidResponseOperationResult(Builder builder) {
    super(EwpOperationResultErrorType.INVALID_RESPONSE, builder);
    this.exception = builder.exception;
  }

  public Exception getException() {
    return exception;
  }

  @Override
  public AbstractEwpClientErrorException toClientException() {
    return new EwpClientInvalidResponseException(
        getRequest(), getResponse(), getResponseAuthenticationResult(), exception);
  }

  @Override
  public String getSummary() {
    return "Server returned an invalid response: " + exception.getMessage();
  }

  public static class Builder extends AbstractErrorEwpOperationResult.Builder<Builder> {

    private final Exception exception;

    public Builder(Exception exception) {
      this.exception = exception;
    }

    @Override
    public Builder getThis() {
      return this;
    }

    public EwpInvalidResponseOperationResult build() {
      return new EwpInvalidResponseOperationResult(this);
    }
  }
}
