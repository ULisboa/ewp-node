package pt.ulisboa.ewp.node.client.ewp.operation.result;

public class EwpProcessorErrorOperationResult extends AbstractEwpOperationResult {

  private Exception exception;

  protected EwpProcessorErrorOperationResult(Builder builder) {
    super(EwpOperationResultType.PROCESSOR_ERROR, builder);
    this.exception = builder.exception;
  };

  public Exception getException() {
    return exception;
  }

  public static class Builder extends AbstractEwpOperationResult.Builder<Builder> {

    private Exception exception;

    public Exception exception() {
      return exception;
    }

    public Builder exception(Exception exception) {
      this.exception = exception;
      return this;
    }

    @Override
    public Builder getThis() {
      return this;
    }

    public EwpProcessorErrorOperationResult build() {
      return new EwpProcessorErrorOperationResult(this);
    }
  }
}
