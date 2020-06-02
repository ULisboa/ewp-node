package pt.ulisboa.ewp.node.utils;

public class StringUtils {

  private StringUtils() {}

  public static String truncateWithSuffix(
      String string, int maxLengthWithoutSuffix, String suffix) {
    if (string.length() > maxLengthWithoutSuffix) {
      return string.substring(0, maxLengthWithoutSuffix) + suffix;
    }
    return string;
  }
}
