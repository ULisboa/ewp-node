package pt.ulisboa.ewp.node.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SemanticVersion {

  private final int majorVersion;
  private final int minorVersion;
  private final int patchVersion;

  SemanticVersion(int majorVersion, int minorVersion, int patchVersion) {
    this.majorVersion = majorVersion;
    this.minorVersion = minorVersion;
    this.patchVersion = patchVersion;
  }

  public int getMajorVersion() {
    return majorVersion;
  }

  public int getMinorVersion() {
    return minorVersion;
  }

  public int getPatchVersion() {
    return patchVersion;
  }

  public static SemanticVersion createFromSemanticVersion(String semanticVersion) {
    Pattern pattern = Pattern.compile("(\\d)\\.(\\d)\\.(\\d)");
    Matcher matcher = pattern.matcher(semanticVersion);
    if (!matcher.matches()) {
      throw new IllegalArgumentException("Invalid semantic version: " + semanticVersion);
    }
    int majorVersion = Integer.parseInt(matcher.group(1));
    int minorVersion = Integer.parseInt(matcher.group(2));
    int patchVersion = Integer.parseInt(matcher.group(3));
    return new SemanticVersion(majorVersion, minorVersion, patchVersion);
  }

  @Override
  public String toString() {
    return majorVersion + "." + minorVersion + "." + patchVersion;
  }
}
