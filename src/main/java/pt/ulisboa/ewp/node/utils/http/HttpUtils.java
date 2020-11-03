package pt.ulisboa.ewp.node.utils.http;

import java.net.URI;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.web.util.UriUtils;

public class HttpUtils {

  private HttpUtils() {}

  public static ExtendedHttpHeaders toExtendedHttpHeaders(MultivaluedMap<String, Object> headers) {
    ExtendedHttpHeaders result = new ExtendedHttpHeaders();
    headers
        .keySet()
        .forEach(
            headerKey ->
                result.put(
                    headerKey,
                    headers.get(headerKey).stream()
                        .map(String::valueOf)
                        .collect(Collectors.toList())));
    return result;
  }

  public static ExtendedHttpHeaders toExtendedHttpHeaders(HttpServletRequest request) {
    ExtendedHttpHeaders headers = new ExtendedHttpHeaders();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      Enumeration<String> headerValuesEnumeration = request.getHeaders(headerName);
      while (headerValuesEnumeration.hasMoreElements()) {
        headers.add(headerName, headerValuesEnumeration.nextElement());
      }
    }
    return headers;
  }

  public static Map<String, String> toHeadersMap(HttpHeaders headers) {
    return headers.entrySet().stream()
        .collect(
            Collectors.toMap(
                Entry::getKey,
                e -> String.join(HttpConstants.HEADERS_COMMA_SEPARATED_LIST_TOKEN, e.getValue())));
  }

  public static Map<String, String> toHeadersMap(HttpServletResponse response) {
    Map<String, String> headers = new HashMap<>();
    Collection<String> headerNames = response.getHeaderNames();
    for (String headerName : headerNames) {
      headers.put(
          headerName.toLowerCase(),
          String.join(
              HttpConstants.HEADERS_COMMA_SEPARATED_LIST_TOKEN, response.getHeaders(headerName)));
    }
    return headers;
  }

  public static String getHostHeaderValue(URI uri) {
    StringBuilder host = new StringBuilder(uri.getHost());
    if (uri.getPort() != -1 && uri.getPort() != 80) {
      host.append(':').append(uri.getPort());
    }
    return host.toString();
  }

  public static String serializeQueryString(Map<String, List<String>> params) {
    return serializeFormData(params);
  }

  public static String serializeFormData(Map<String, List<String>> params) {
    final StringBuilder sb = new StringBuilder();

    try {
      for (Map.Entry<String, List<String>> entry : params.entrySet()) {
        for (String value : entry.getValue()) {
          if (sb.length() > 0) {
            sb.append('&');
          }
          sb.append(entry.getKey());
          if (value != null) {
            sb.append('=');
            sb.append(value);
          }
        }
      }
    } catch (Exception e) {
      LoggerFactory.getLogger(HttpUtils.class).error("Failed to convert form data ", e);
    }

    return UriUtils.encodeQuery(sb.toString(), "UTF-8");
  }
}
