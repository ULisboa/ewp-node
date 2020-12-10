package pt.ulisboa.ewp.node.client.ewp.exception;

public class NoEwpApiForHeiIdAndMajorVersionException extends NoEwpApiForHeiIdException {

  private final int majorVersion;

  public NoEwpApiForHeiIdAndMajorVersionException(
      String heiId, String ewpApiName, int majorVersion) {
    super(heiId, ewpApiName);
    this.majorVersion = majorVersion;
  }

  public int getMajorVersion() {
    return majorVersion;
  }

  @Override
  public String getMessage() {
    return "HEI with ID "
        + getHeiId()
        + " does not implement "
        + getEwpApiName()
        + " with major version "
        + majorVersion;
  }
}
