package pt.ulisboa.ewp.node.utils;

public class ByteArrayUtils {

  private ByteArrayUtils() {}

  public static byte[] truncateWithSuffix(byte[] bytes, int maxLengthWithSuffix, byte[] suffix) {
    if (bytes.length + suffix.length > maxLengthWithSuffix) {
      byte[] result = new byte[maxLengthWithSuffix];
      System.arraycopy(bytes, 0, result, 0, maxLengthWithSuffix - suffix.length);
      System.arraycopy(suffix, 0, result, maxLengthWithSuffix - suffix.length, suffix.length);
      return result;
    }
    return bytes;
  }
}
