package pt.ulisboa.ewp.node.exception.ewp;

public class EwpUnknownHeiIdException extends EwpBadRequestException {

  public EwpUnknownHeiIdException(String heiId) {
    super("Unknown HEI ID: " + heiId);
  }

  @Override
  public String toString() {
    return "EwpUnknownHeiIdException{" + "developerMessage='" + getDeveloperMessage() + '\'' + '}';
  }
}
