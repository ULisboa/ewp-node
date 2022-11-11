package pt.ulisboa.ewp.node.api.host.forward.ewp.filter;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.filter.ForwardEwpApiJwtTokenAuthenticationFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.service.http.log.host.HostHttpCommunicationLogService;

/**
 * Filter that wraps Forward EWP API requests around a wrapper, that is able to cache the request
 * body. Also, it wraps the response around a wrapper, that is able to cache the response body.
 * <p>
 * After passing further into the filter chain the request, it logs the request and response
 * obtained.
 */
@Configuration
@Order(Integer.MIN_VALUE)
public class ForwardEwpApiRequestFilter extends OncePerRequestFilter {

  private HostHttpCommunicationLogService hostCommunicationLogService;

  public ForwardEwpApiRequestFilter(HostHttpCommunicationLogService hostCommunicationLogService) {
    this.hostCommunicationLogService = hostCommunicationLogService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith(ForwardEwpApiConstants.API_BASE_URI);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    ZonedDateTime startProcessingDateTime = ZonedDateTime.now();
    ContentCachingRequestWrapper requestWrapper = new ContentCachingRequestWrapper(
        request);
    ContentCachingResponseWrapper responseWrapper =
        new ContentCachingResponseWrapper(response);
    filterChain.doFilter(requestWrapper, responseWrapper);
    ZonedDateTime endProcessingDateTime = ZonedDateTime.now();

    Host host = (Host) request
        .getAttribute(ForwardEwpApiJwtTokenAuthenticationFilter.REQUEST_ATTRIBUTE_HOST_NAME);

    logCommunication(
        host,
        requestWrapper,
        responseWrapper,
        startProcessingDateTime,
        endProcessingDateTime,
        "");

    responseWrapper.copyBodyToResponse();
  }

  private void logCommunication(
      Host host,
      ContentCachingRequestWrapper request,
      ContentCachingResponseWrapper response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations) {
    hostCommunicationLogService.logCommunicationFromHost(host,
        request, response, startProcessingDateTime, endProcessingDateTime, observations);
  }
}
