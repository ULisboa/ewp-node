package pt.ulisboa.ewp.node.client.ewp.exception;

import java.io.PrintWriter;
import java.io.StringWriter;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.response.EwpResponse;

/**
 * Request/Response processing failed for some reason.
 */
public class EwpClientProcessorException extends EwpClientErrorException {

  private final Exception exception;

  public EwpClientProcessorException(EwpRequest request, EwpResponse response,
      Exception exception) {
    super(request, response);
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }

  @Override
  public String getDetailedMessage() {
    StringWriter exceptionStringWriter = new StringWriter();
    exception.printStackTrace(new PrintWriter(exceptionStringWriter));
    return "Processor error: "
        + System.lineSeparator()
        + exceptionStringWriter.toString().substring(0, 1000);
  }

  @Override
  public String getMessage() {
    return "Processor error: " + exception.getMessage();
  }
}
