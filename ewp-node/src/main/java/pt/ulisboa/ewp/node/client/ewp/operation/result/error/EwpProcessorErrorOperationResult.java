package pt.ulisboa.ewp.node.client.ewp.operation.result.error;

import java.io.PrintWriter;
import java.io.StringWriter;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;

public class EwpProcessorErrorOperationResult extends AbstractErrorEwpOperationResult {

  private final Exception exception;

  protected EwpProcessorErrorOperationResult(Builder builder) {
    super(EwpOperationResultErrorType.PROCESSOR_ERROR, builder);
    this.exception = builder.exception;
  }

  public Exception getException() {
    return exception;
  }

  @Override
  public AbstractEwpClientErrorException toClientException() {
    return new EwpClientProcessorException(getException());
  }

  @Override
  public String getSummary() {
    StringWriter exceptionStringWriter = new StringWriter();
    exception.printStackTrace(new PrintWriter(exceptionStringWriter));
    return "Processor error: "
        + System.lineSeparator()
        + exceptionStringWriter.toString().substring(0, 1000);
  }

  public static class Builder extends AbstractErrorEwpOperationResult.Builder<Builder> {

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
