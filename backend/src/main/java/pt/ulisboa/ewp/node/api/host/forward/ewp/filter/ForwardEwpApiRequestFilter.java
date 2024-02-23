package pt.ulisboa.ewp.node.api.host.forward.ewp.filter;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.filter.ForwardEwpApiJwtTokenAuthenticationFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.wrapper.ForwardEwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContext;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.communication.log.http.host.HostHttpCommunicationLogService;

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

  private static final Logger LOG = LoggerFactory.getLogger(ForwardEwpApiRequestFilter.class);

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
    ForwardEwpApiHttpRequestWrapper requestWrapper = new ForwardEwpApiHttpRequestWrapper(request);
    ContentCachingResponseWrapper responseWrapper =
        new ContentCachingResponseWrapper(response);

    HttpCommunicationFromHostLog newCommunicationLog = createCommunicationLog(
        startProcessingDateTime, requestWrapper);
    CommunicationContextHolder.setContext(new CommunicationContext(null, newCommunicationLog));

    filterChain.doFilter(requestWrapper, responseWrapper);

    HostForwardEwpApiClient hostForwardEwpApiClient = (HostForwardEwpApiClient) request
        .getAttribute(
            ForwardEwpApiJwtTokenAuthenticationFilter.REQUEST_ATTRIBUTE_HOST_FORWARD_EWP_API_CLIENT_NAME);
    updateCommunicationLogWithHostForwardEwpApiClientAndResponse(
        (HttpCommunicationFromHostLog)
            CommunicationContextHolder.getContext().getCurrentCommunicationLog(),
        hostForwardEwpApiClient,
        responseWrapper);
    CommunicationContextHolder.clearContext();

    responseWrapper.copyBodyToResponse();
  }

  private void updateCommunicationLogWithHostForwardEwpApiClientAndResponse(
      HttpCommunicationFromHostLog communicationLog,
      HostForwardEwpApiClient hostForwardEwpApiClient, ContentCachingResponseWrapper response) {

    communicationLog.setHost(
        hostForwardEwpApiClient != null ? hostForwardEwpApiClient.getHost() : null);
    communicationLog.setHostForwardEwpApiClient(hostForwardEwpApiClient);
    communicationLog.setResponse(hostCommunicationLogService.toHttpResponseLog(response));
    communicationLog.setEndProcessingDateTime(ZonedDateTime.now());
    if (!hostCommunicationLogService.persist(communicationLog)) {
      throw new IllegalStateException(
          "Failed to update communication log #" + communicationLog.getId()
              + "with response data");
    }
  }

  private HttpCommunicationFromHostLog createCommunicationLog(
      ZonedDateTime startProcessingDateTime, ContentCachingRequestWrapper requestWrapper) {
    HttpCommunicationFromHostLog newCommunicationLog;
    try {
      newCommunicationLog = hostCommunicationLogService.logCommunicationFromHost(
          null, null,
          requestWrapper,
          null,
          startProcessingDateTime,
          null,
          "", null);
    } catch (DomainException | IOException e) {
      throw new IllegalStateException(e);
    }
    return newCommunicationLog;
  }
}
