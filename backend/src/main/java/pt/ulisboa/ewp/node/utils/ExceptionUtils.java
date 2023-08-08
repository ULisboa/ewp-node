package pt.ulisboa.ewp.node.utils;

public class ExceptionUtils {

  private ExceptionUtils() {}

  public static String getStackTraceAsString(
      Throwable throwable, int maxNumberOfStackTraceLinesPerLevel) {
    return getStackTraceAsString(throwable, null, "", maxNumberOfStackTraceLinesPerLevel);
  }

  private static String getStackTraceAsString(
      Throwable throwable, Throwable parentThrowable, String caption, int maxNumberOfStackTraceLinesPerLevel) {
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(caption).append(throwable.toString()).append(System.lineSeparator());

    StackTraceElement[] stackTrace = throwable.getStackTrace();
    boolean foundCommonElement = false;
    int firstStackTraceElementInCommonIndex = -1;
    if (parentThrowable != null) {
      int currentStackTraceIndex = stackTrace.length - 1;
      StackTraceElement[] parentStackTraceElements = parentThrowable.getStackTrace();
      int currentParentStackTraceIndex = parentStackTraceElements.length - 1;
      while (currentStackTraceIndex >= 0
          && currentParentStackTraceIndex >= 0
          && stackTrace[currentStackTraceIndex].equals(
              parentStackTraceElements[currentParentStackTraceIndex])) {
        currentStackTraceIndex--;
        currentParentStackTraceIndex--;
        foundCommonElement = true;
      }

      firstStackTraceElementInCommonIndex = foundCommonElement ? currentStackTraceIndex + 1 : -1;
    }

    int stackTraceIndex = 0;
    int limitStackTraceIndexExclusive = Math.min(stackTrace.length, maxNumberOfStackTraceLinesPerLevel);
    if (foundCommonElement) {
      limitStackTraceIndexExclusive = Math.min(limitStackTraceIndexExclusive, firstStackTraceElementInCommonIndex);
    }
    while (stackTraceIndex < limitStackTraceIndexExclusive) {
      StackTraceElement stackTraceElement = stackTrace[stackTraceIndex];
      stringBuilder.append("\tat ").append(stackTraceElement).append(System.lineSeparator());
      stackTraceIndex++;
    }

    if (stackTraceIndex < stackTrace.length) {
      int remainingLines = stackTrace.length - stackTraceIndex;
      stringBuilder
          .append("\t... ")
          .append(remainingLines)
          .append(" more")
          .append(System.lineSeparator());
    }

    for (Throwable suppresedElement : throwable.getSuppressed()) {
      stringBuilder.append(
              getStackTraceAsString(
                      suppresedElement, throwable, "Suppressed: ", maxNumberOfStackTraceLinesPerLevel));
    }

    Throwable cause = throwable.getCause();
    if (cause != null) {
      stringBuilder.append(
          getStackTraceAsString(cause, throwable, "Caused by: ", maxNumberOfStackTraceLinesPerLevel));
    }

    return stringBuilder.toString();
  }
}
