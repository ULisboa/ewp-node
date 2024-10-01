package pt.ulisboa.ewp.node.api.ewp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;

/**
 * Filter that wraps EWP API requests around an EwpHttpRequestWrapper and responses as
 * ContentCachingResponseWrapper.
 */
@Configuration
@Order(Integer.MIN_VALUE)
public class EwpApiRequestAndResponseWrapperFilter extends OncePerRequestFilter {

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith(EwpApiConstants.API_BASE_URI)
        && !request.getRequestURI().startsWith(EwpApiConstants.REST_BASE_URI);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    EwpApiHttpRequestWrapper ewpRequest = new EwpApiHttpRequestWrapper(request);
    ContentCachingResponseWrapper contentCachingResponseWrapper =
        new ContentCachingResponseWrapper(response);

    filterChain.doFilter(ewpRequest, contentCachingResponseWrapper);

    contentCachingResponseWrapper.copyBodyToResponse();
  }
}
