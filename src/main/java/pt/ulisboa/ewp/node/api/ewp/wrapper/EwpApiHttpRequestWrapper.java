package pt.ulisboa.ewp.node.api.ewp.wrapper;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;
import javax.servlet.http.HttpServletRequest;
import org.apache.catalina.connector.Request;
import org.apache.catalina.connector.RequestFacade;
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
  private String body;

  private EwpApiHostAuthenticationToken authenticationToken;
  private Predicate<String> headerFilter = headerName -> true;

  public EwpApiHttpRequestWrapper(HttpServletRequest request) throws IOException {
    super(request);

    this.originalQueryString = request.getQueryString();

    sanitizeRequest(request);
    initBody(request);
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
      log.error("Failed to obtain internal request instance", e);
      throw new IllegalStateException("Failed to obtain internal request instance", e);
    }
  }

  private void initBody(HttpServletRequest request) throws IOException {
    if (HttpMethod.POST.matches(request.getMethod())) {
      this.body =
          new StringHttpMessageConverter().read(String.class, new ServletServerHttpRequest(this));
    } else {
      this.body = "";
    }
  }

  public String getOriginalQueryString() {
    return originalQueryString;
  }

  /**
   * Sets an header filter by name. Tests are done against lower case header names (for
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
    return body;
  }
}
