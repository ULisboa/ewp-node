package pt.ulisboa.ewp.node.utils.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.springframework.http.HttpHeaders;

/**
 * Data structure representing HTTP request and response headers. It extends from {@link
 * HttpHeaders} to provide additional helpful methods, such as, Digest Headers.
 */
public class ExtendedHttpHeaders extends HttpHeaders {

  public static final String HEADER_DIGEST_SPLIT_PATTERN = ",[ ]+";

  /**
   * Returns the digest value corresponding to the digest algorithm provided, if existing.
   *
   * @param digestAlgorithm Digest algorithm to look up.
   * @return The digest value corresponding to the digest algorithm provided.
   */
  public String getDigestValue(String digestAlgorithm) {
    return getDigestValues().get(digestAlgorithm);
  }

  /**
   * Returns a case insensitive map of digest algorithms to digest values.
   *
   * @return A case insensitive map of digest algorithms to digest values.
   */
  public Map<String, String> getDigestValues() {
    CaseInsensitiveMap<String, String> result = new CaseInsensitiveMap<>();
    getOrDefault(HttpConstants.HEADER_DIGEST, new ArrayList<>()).stream()
        .flatMap(value -> Arrays.stream(value.split(HEADER_DIGEST_SPLIT_PATTERN)))
        .forEach(
            value ->
                result.putIfAbsent(
                    value.substring(0, value.indexOf('=')),
                    value.substring(value.indexOf('=') + 1)));
    return result;
  }
}
