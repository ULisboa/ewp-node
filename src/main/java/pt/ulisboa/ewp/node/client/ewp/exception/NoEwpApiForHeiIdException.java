package pt.ulisboa.ewp.node.client.ewp.exception;

public class NoEwpApiForHeiIdException extends RuntimeException {

  private String heiId;
  private String ewpApiName;

  public NoEwpApiForHeiIdException(String heiId, String ewpApiName) {
    this.heiId = heiId;
    this.ewpApiName = ewpApiName;
  }

  public String getHeiId() {
    return heiId;
  }

  public String getEwpApiName() {
    return ewpApiName;
  }

  @Override
  public String getMessage() {
    return "HEI with ID " + heiId + " does not implement " + ewpApiName;
  }
}
