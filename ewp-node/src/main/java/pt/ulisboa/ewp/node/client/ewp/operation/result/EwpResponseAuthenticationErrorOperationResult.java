package pt.ulisboa.ewp.node.client.ewp.operation.result;

public class EwpResponseAuthenticationErrorOperationResult extends AbstractEwpOperationResult {

  protected EwpResponseAuthenticationErrorOperationResult(Builder builder) {
    super(EwpOperationResultType.RESPONSE_AUTHENTICATION_ERROR, builder);
  };

  public static class Builder extends AbstractEwpOperationResult.Builder<Builder> {

    @Override
    public Builder getThis() {
      return this;
    }

    public EwpResponseAuthenticationErrorOperationResult build() {
      return new EwpResponseAuthenticationErrorOperationResult(this);
    }
  }
}
