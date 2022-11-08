package pt.ulisboa.ewp.node.exception.ewp;

public class EwpNotFoundException extends RuntimeException {

  private final String developerMessage;

  public EwpNotFoundException(String developerMessage) {
    this.developerMessage = developerMessage;
  }

  public String getDeveloperMessage() {
    return developerMessage;
  }

  @Override
  public String getMessage() {
    return getDeveloperMessage();
  }

  @Override
  public String toString() {
    return "EwpNotFoundException{" + "developerMessage='" + developerMessage + '\'' + '}';
  }
}
