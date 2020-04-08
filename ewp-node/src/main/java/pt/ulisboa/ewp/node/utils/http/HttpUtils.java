package pt.ulisboa.ewp.node.utils.http;

import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.MultivaluedMap;

import org.slf4j.LoggerFactory;

public class HttpUtils {

  private HttpUtils() {}

  public static HttpHeadersMap getCaseInsensitiveHeadersMap(
      MultivaluedMap<String, Object> headers) {
    HttpHeadersMap result = new HttpHeadersMap();
    headers
        .keySet()
        .forEach(
            headerKey ->
                result.put(
                    headerKey,
                    headers.get(headerKey).stream()
                        .map(h -> (String) h)
                        .collect(
                            Collectors.joining(HttpConstants.HEADERS_COMMA_SEPARATED_LIST_TOKEN))));
    return result;
  }

  public static HttpHeadersMap getCaseInsensitiveHeadersMap(HttpServletRequest request) {
    HttpHeadersMap headers = new HttpHeadersMap();
    Enumeration<String> headerNames = request.getHeaderNames();
    while (headerNames.hasMoreElements()) {
      String headerName = headerNames.nextElement();
      Collection<String> headerValues = new ArrayList<>();
      Enumeration<String> headerValuesEnumeration = request.getHeaders(headerName);
      while (headerValuesEnumeration.hasMoreElements()) {
        headerValues.add(headerValuesEnumeration.nextElement());
      }
      headers.put(
          headerName.toLowerCase(),
          String.join(HttpConstants.HEADERS_COMMA_SEPARATED_LIST_TOKEN, headerValues));
    }
    return headers;
  }

  public static Map<String, String> getHeaders(HttpServletResponse response) {
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
          sb.append(URLEncoder.encode(entry.getKey(), "UTF-8"));
          if (value != null) {
            sb.append('=');
            sb.append(URLEncoder.encode(value, "UTF-8"));
          }
        }
      }
    } catch (Exception e) {
      LoggerFactory.getLogger(HttpUtils.class).error("Failed to convert form data ", e);
    }

    return sb.toString();
  }
}
