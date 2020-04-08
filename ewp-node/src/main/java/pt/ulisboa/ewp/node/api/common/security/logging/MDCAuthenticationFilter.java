package pt.ulisboa.ewp.node.api.common.security.logging;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.jboss.logging.MDC;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * A filter that adds authentication information to Slf4j's MDC (Mapped Diagnostic Context).
 *
 * <p>Reference:
 * https://blog.trifork.com/2013/06/06/adding-user-info-to-log-entries-in-a-multi-user-app-using-mapped-diagnostic-context/
 */
public class MDCAuthenticationFilter implements Filter {

  private static final String KEY_USERNAME = "username";

  private static final String VALUE_ANONYMOUS = "anonymous";

  @Override
  public void init(FilterConfig filterConfig) {
    // do nothing.
  }

  @Override
  public void doFilter(
      ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
      throws IOException, ServletException {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication != null) {
      MDC.put(KEY_USERNAME, authentication.getName());
    } else {
      MDC.put(KEY_USERNAME, VALUE_ANONYMOUS);
    }

    try {
      filterChain.doFilter(servletRequest, servletResponse);
    } finally {
      MDC.remove(KEY_USERNAME);
    }
  }

  @Override
  public void destroy() {}
}
