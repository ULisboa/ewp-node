package pt.ulisboa.ewp.node.client.ewp.exception;

public class EwpClientProcessorException extends Exception {

  private Exception exception;

  public EwpClientProcessorException(Exception exception) {
    this.exception = exception;
  }

  public Exception getException() {
    return exception;
  }

  @Override
  public String getMessage() {
    return "EWP Client failed to process request: " + exception.getMessage();
  }
}
