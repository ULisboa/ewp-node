package pt.ulisboa.ewp.node.utils.http;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.collections4.map.CaseInsensitiveMap;

/** A case insensitive Header key to value map. */
public class HttpHeadersMap extends CaseInsensitiveMap<String, String> {

  public HttpHeadersMap() {
    super();
  }

  public HttpHeadersMap(HttpHeadersMap headersMap) {
    super(headersMap);
  }

  public HttpHeadersMap header(String key, String value) {
    this.put(key, value);
    return this;
  }

  public List<String> getAsList(String key) {
    String value = get(key);
    if (value == null) {
      return Collections.emptyList();
    }
    return Arrays.stream(value.split(HttpConstants.HEADERS_COMMA_SEPARATED_LIST_TOKEN))
        .map(String::trim)
        .collect(Collectors.toList());
  }
}
