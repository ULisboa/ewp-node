package pt.ulisboa.ewp.node.service.ewp.iia.hash;

public class HashCalculationResult {

  private final String hash;
  private final String hashedString;

  public HashCalculationResult(String hash, String hashedString) {
    this.hash = hash;
    this.hashedString = hashedString;
  }

  public String getHash() {
    return hash;
  }

  public String getHashedString() {
    return hashedString;
  }
}
