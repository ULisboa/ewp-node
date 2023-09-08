package pt.ulisboa.ewp.node.exception.ewp;

public class EwpUnknownOrganizationalUnitCodeException extends EwpBadRequestException {

  public EwpUnknownOrganizationalUnitCodeException(String heiId, String ounitCode) {
    super("Unknown Organizational Unit code '" + ounitCode + "' for HEI ID '" + heiId + "'");
  }

  @Override
  public String toString() {
    return "EwpUnknownOrganizationalUnitCodeException{"
        + "developerMessage='"
        + getDeveloperMessage()
        + '\''
        + '}';
  }
}
