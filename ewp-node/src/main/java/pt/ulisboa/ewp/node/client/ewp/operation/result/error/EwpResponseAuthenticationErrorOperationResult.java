package pt.ulisboa.ewp.node.client.ewp.operation.result.error;

import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientResponseAuthenticationFailedException;

public class EwpResponseAuthenticationErrorOperationResult extends AbstractErrorEwpOperationResult {

  protected EwpResponseAuthenticationErrorOperationResult(Builder builder) {
    super(EwpOperationResultErrorType.RESPONSE_AUTHENTICATION_ERROR, builder);
  }

  @Override
  public AbstractEwpClientErrorException toClientException() {
    return new EwpClientResponseAuthenticationFailedException(
        getRequest(), getResponse(), getResponseAuthenticationResult());
  }

  @Override
  public String getSummary() {
    return "Response failed authentication: " + getResponseAuthenticationResult().getErrorMessage();
  }

  public static class Builder extends AbstractErrorEwpOperationResult.Builder<Builder> {

    @Override
    public Builder getThis() {
      return this;
    }

    public EwpResponseAuthenticationErrorOperationResult build() {
      return new EwpResponseAuthenticationErrorOperationResult(this);
    }
  }
}
