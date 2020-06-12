package pt.ulisboa.ewp.node.exception.ewp;

public class EwpBadRequestException extends RuntimeException {

  private final String developerMessage;

  public EwpBadRequestException(String developerMessage) {
    this.developerMessage = developerMessage;
  }

  public String getDeveloperMessage() {
    return developerMessage;
  }

  @Override
  public String toString() {
    return "EwpBadRequestException{" + "developerMessage='" + developerMessage + '\'' + '}';
  }
}
