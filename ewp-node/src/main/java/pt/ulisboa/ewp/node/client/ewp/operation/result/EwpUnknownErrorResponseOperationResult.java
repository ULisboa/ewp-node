package pt.ulisboa.ewp.node.client.ewp.operation.result;

public class EwpUnknownErrorResponseOperationResult extends AbstractEwpOperationResult {

  private String error;

  protected EwpUnknownErrorResponseOperationResult(Builder builder) {
    super(EwpOperationResultType.UNKNOWN_ERROR_RESPONSE, builder);
    this.error = builder.error;
  };

  public String getError() {
    return error;
  }

  public static class Builder extends AbstractEwpOperationResult.Builder<Builder> {

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
