package pt.ulisboa.ewp.node.exception.ewp;

public class EwpUnknownOrganizationalUnitIdException extends EwpBadRequestException {

  public EwpUnknownOrganizationalUnitIdException(String heiId, String ounitId) {
    super("Unknown Organizational Unit ID '" + ounitId + "' for HEI ID '" + heiId + "'");
  }

  @Override
  public String toString() {
    return "EwpUnknownOrganizationalUnitIdException{" + "developerMessage='" + getDeveloperMessage() + '\'' + '}';
  }

}
