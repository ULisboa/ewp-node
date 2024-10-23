package pt.ulisboa.ewp.node.api.ewp.wrapper;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Predicate;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpMethod;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.ReflectionUtils;
import org.springframework.web.util.ContentCachingRequestWrapper;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;

/**
 * A wrapper of HttpServletRequest interface that allows multiple body readings using the method
 * getBody(). The body obtained is as was sent by client. It also allows to filter headers according
 * to a predicate. When used jointly with an EWP authentication procedure it provides authentication
 * details.
 */
public class EwpApiHttpRequestWrapper extends ContentCachingRequestWrapper {

  private static final Logger log = LoggerFactory.getLogger(EwpApiHttpRequestWrapper.class);
  private static final String REQUEST_FIELD_NAME = "request";

  private final String originalQueryString;
  private String cachedBody;
  private Map<String, String[]> parameterMap;

  private EwpApiHostAuthenticationToken authenticationToken;
  private Predicate<String> headerFilter = headerName -> true;

  public EwpApiHttpRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);

    this.originalQueryString = request.getQueryString();

    sanitizeRequest(request);
    initCachedBody(request);
    initParameterMap(request);
  }

  private void sanitizeRequest(HttpServletRequest request) {
    if (HttpMethod.POST.matches(request.getMethod())) {
      // NOTE: EWP requires to ignore query parameters on a POST request.
      // However, Tomcat automatically considers them so it is necessary to remove them from the
      // request
      if (request instanceof RequestFacade) {
        cleanRequestQueryString((RequestFacade) request);
      } else {
        log.warn(
            "Unknown request type, will not sanitize request: {}", request.getClass().getName());
      }
    }
  }

  private void cleanRequestQueryString(RequestFacade request) {
    try {
      Field field = ReflectionUtils.findField(request.getClass(), REQUEST_FIELD_NAME);
      if (field == null) {
        throw new NoSuchFieldException("Could not find field: " + REQUEST_FIELD_NAME);
      }
      ReflectionUtils.makeAccessible(field);
      Request internalRequest = (Request) field.get(request);
      internalRequest.getCoyoteRequest().queryString().setString("");
    } catch (IllegalAccessException | NoSuchFieldException e) {
      throw new IllegalStateException("Failed to obtain internal request instance", e);
    }
  }

  private void initCachedBody(HttpServletRequest request) {
    try {
      if (HttpMethod.POST.matches(request.getMethod())
          || HttpMethod.PUT.matches(request.getMethod())
          || HttpMethod.PATCH.matches(request.getMethod())) {
        StringHttpMessageConverter converter =
            new StringHttpMessageConverter(StandardCharsets.UTF_8);
        this.cachedBody = converter.read(String.class, new ServletServerHttpRequest(request));
        if (StringUtils.isEmpty(this.cachedBody)) {
          this.cachedBody =
              new String(request.getInputStream().readAllBytes(), Charset.defaultCharset());
        }
      } else {
        this.cachedBody = "";
      }
    } catch (IOException e) {
      throw new IllegalStateException(e);
    }
  }

  private void initParameterMap(HttpServletRequest request) {
    this.parameterMap = new HashMap<>(request.getParameterMap());

    // NOTE: Only try to parse parameters if the original map is empty
    if (this.parameterMap.isEmpty()) {
      // Populate parameter map if content type is application/x-www-form-urlencoded
      if ("application/x-www-form-urlencoded".equals(request.getContentType())) {
        parseParameters(this.cachedBody);
      }
    }
  }

  private void parseParameters(String body) {
    String[] pairs = body.split("&");
    for (String pair : pairs) {
      String[] keyValue = pair.split("=");
      if (keyValue.length > 1) {
        String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
        String value = URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8);
        appendValueToParameterValues(key, value);
      } else if (keyValue.length == 1) {
        String key = URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8);
        appendValueToParameterValues(key, "");
      }
    }
  }

  private void appendValueToParameterValues(String key, String valueToAppend) {
    String[] currentParameterValues = this.parameterMap.getOrDefault(key, new String[0]);
    String[] updatedParameterValues = ArrayUtils.add(currentParameterValues, valueToAppend);
    this.parameterMap.put(key, updatedParameterValues);
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    return new CachedBodyServletInputStream(this.cachedBody.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public BufferedReader getReader() throws IOException {
    ByteArrayInputStream byteArrayInputStream =
        new ByteArrayInputStream(this.cachedBody.getBytes(StandardCharsets.UTF_8));
    return new BufferedReader(new InputStreamReader(byteArrayInputStream));
  }

  @Override
  public Map<String, String[]> getParameterMap() {
    return this.parameterMap;
  }

  @Override
  public String[] getParameterValues(String name) {
    return this.parameterMap.get(name);
  }

  @Override
  public String getParameter(String name) {
    String[] values = this.parameterMap.get(name);
    return (values != null && values.length > 0) ? values[0] : null;
  }

  public String getOriginalQueryString() {
    return originalQueryString;
  }

  /**
   * Sets a header filter by name. Tests are done against lower case header names (for
   * case-insensitive support).
   */
  public void setHeadersToIncludeFilter(Predicate<String> headerFilter) {
    this.headerFilter = headerFilter;
  }

  public EwpApiHostAuthenticationToken getAuthenticationToken() {
    return authenticationToken;
  }

  public void setAuthenticationToken(EwpApiHostAuthenticationToken authenticationToken) {
    this.authenticationToken = authenticationToken;
  }

  @Override
  public Enumeration<String> getHeaderNames() {
    Enumeration<String> unfilteredHeaderNames = super.getHeaderNames();
    Set<String> filteredHeaderNames = new HashSet<>();
    while (unfilteredHeaderNames.hasMoreElements()) {
      String headerName = unfilteredHeaderNames.nextElement();
      if (isToIncludeHeader(headerName)) {
        filteredHeaderNames.add(headerName);
      }
    }
    return Collections.enumeration(filteredHeaderNames);
  }

  @Override
  public Enumeration<String> getHeaders(String name) {
    if (isToIncludeHeader(name)) {
      return super.getHeaders(name);
    }
    return Collections.enumeration(Collections.emptySet());
  }

  @Override
  public String getHeader(String name) {
    if (isToIncludeHeader(name)) {
      return super.getHeader(name);
    }
    return null;
  }

  @Override
  public int getIntHeader(String name) {
    if (isToIncludeHeader(name)) {
      return super.getIntHeader(name);
    }
    return -1;
  }

  @Override
  public long getDateHeader(String name) {
    if (isToIncludeHeader(name)) {
      return super.getDateHeader(name);
    }
    return -1;
  }

  private boolean isToIncludeHeader(String name) {
    return headerFilter.test(name.toLowerCase());
  }

  /** Returns request body. It may be called multiple times. */
  public String getBody() {
    return cachedBody;
  }

  public static class CachedBodyServletInputStream extends ServletInputStream {

    private final InputStream cachedBodyInputStream;

    public CachedBodyServletInputStream(byte[] cacheBody) {
      this.cachedBodyInputStream = new ByteArrayInputStream(cacheBody);
    }

    /**
     * Indicates whether InputStream has more data to read or not.
     *
     * @return true when zero bytes available to read
     */
    @Override
    public boolean isFinished() {
      try {
        return cachedBodyInputStream.available() == 0;
      } catch (IOException e) {
        log.error("Failed to check if InputStream has more data or not", e);
      }
      return false;
    }

    /**
     * Indicates whether InputStream is ready for reading or not. Since we've already copied
     * InputStream in a byte array, we'll return true to indicate that it's always available.
     *
     * @return true
     */
    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener listener) {
      throw new UnsupportedOperationException();
    }

    @Override
    public int read() throws IOException {
      return cachedBodyInputStream.read();
    }
  }
}
