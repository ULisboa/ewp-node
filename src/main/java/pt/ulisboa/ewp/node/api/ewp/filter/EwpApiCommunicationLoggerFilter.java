package pt.ulisboa.ewp.node.api.ewp.filter;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import pt.ulisboa.ewp.node.api.ewp.EwpCommunicationContextHolder;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.service.communication.log.http.ewp.EwpHttpCommunicationLogService;

/**
 * Filter that logs an EWP request from some node of the EWP network.
 * <p>
 * It is run after EwpApiRequestAndResponseWrapperFilter, as it needs wrapper classes on
 * request/response.
 */
@Configuration
@Order(Integer.MIN_VALUE + 1)
public class EwpApiCommunicationLoggerFilter extends OncePerRequestFilter {

  private final EwpHttpCommunicationLogService ewpCommunicationLogService;

  public EwpApiCommunicationLoggerFilter(
      EwpHttpCommunicationLogService ewpCommunicationLogService) {
    this.ewpCommunicationLogService = ewpCommunicationLogService;
  }

  @Override
  protected boolean shouldNotFilter(HttpServletRequest request) {
    return !request.getRequestURI().startsWith(EwpApiConstants.API_BASE_URI)
        && !request.getRequestURI().startsWith(EwpApiConstants.REST_BASE_URI);
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {

    ZonedDateTime startProcessingDateTime = ZonedDateTime.now();

    filterChain.doFilter(request, response);

    if (!(request instanceof EwpApiHttpRequestWrapper)) {
      throw new IllegalStateException(
          "Expected request as EwpApiHttpRequestWrapper but got: " + request.getClass().getName());
    }
    EwpApiHttpRequestWrapper ewpRequest = (EwpApiHttpRequestWrapper) request;

    if (!(response instanceof ContentCachingResponseWrapper)) {
      throw new IllegalStateException(
          "Expected response as ContentCachingResponseWrapper but got: " + response.getClass()
              .getName());
    }
    ContentCachingResponseWrapper contentCachingResponseWrapper = (ContentCachingResponseWrapper) response;

    ZonedDateTime endProcessingDateTime = ZonedDateTime.now();

    String observation = EwpCommunicationContextHolder.getInstance(request).getObservation();

    ewpCommunicationLogService.logCommunicationFromEwpNode(
        ewpRequest,
        contentCachingResponseWrapper,
        startProcessingDateTime,
        endProcessingDateTime,
        observation, null);
  }
}
