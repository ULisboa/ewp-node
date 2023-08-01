package pt.ulisboa.ewp.node.utils;

public class StringUtils {

  private StringUtils() {
  }

  /**
   * Break a given text by ensuring that every line of the text (ended by lineSeparator) is at most
   * length maximumLineLength.
   *
   * @param text              The text to break
   * @param lineSeparator     Line separator
   * @param maximumLineLength The maximum line length of every line of the resulting text.
   */
  public static String breakTextWithLineLengthLimit(String text, String lineSeparator,
      int maximumLineLength) {
    StringBuilder resultBuilder = new StringBuilder(text);
    int currentIndex = 0;
    while (currentIndex < resultBuilder.length()) {
      int nextLineSeparatorIndex = resultBuilder.indexOf(lineSeparator, currentIndex);
      if (nextLineSeparatorIndex == -1) {
        currentIndex += maximumLineLength;
        while (currentIndex < resultBuilder.length()) {
          resultBuilder.insert(currentIndex, lineSeparator);
          currentIndex += maximumLineLength + 1;
        }

      } else {

        currentIndex = Math.min(nextLineSeparatorIndex, currentIndex + maximumLineLength);
        while (currentIndex < nextLineSeparatorIndex) {
          resultBuilder.insert(currentIndex, lineSeparator);
          currentIndex += maximumLineLength;
        }

        currentIndex = nextLineSeparatorIndex + 1;
      }
    }
    return resultBuilder.toString();
  }

  public static String truncateWithSuffix(
      String string, int maxLengthWithSuffix, String suffix) {
    if (string.length() + suffix.length() > maxLengthWithSuffix) {
      return string.substring(0, maxLengthWithSuffix - suffix.length()) + suffix;
    }
    return string;
  }
}
