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
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContext;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.communication.log.http.ewp.EwpHttpCommunicationLogService;

/**
 * Filter that logs an EWP request from some node of the EWP network.
 *
 * <p>It is run after EwpApiRequestAndResponseWrapperFilter, as it needs wrapper classes on
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

    if (!(request instanceof EwpApiHttpRequestWrapper)) {
      throw new IllegalStateException(
          "Expected request as EwpApiHttpRequestWrapper but got: " + request.getClass().getName());
    }
    EwpApiHttpRequestWrapper ewpRequest = (EwpApiHttpRequestWrapper) request;

    if (!(response instanceof ContentCachingResponseWrapper)) {
      throw new IllegalStateException(
          "Expected response as ContentCachingResponseWrapper but got: "
              + response.getClass().getName());
    }
    ContentCachingResponseWrapper contentCachingResponseWrapper =
        (ContentCachingResponseWrapper) response;

    HttpCommunicationFromEwpNodeLog newCommunicationLog =
        createCommunicationLog(startProcessingDateTime, ewpRequest);
    if (newCommunicationLog == null) {
      throw new IllegalStateException("Could not build context");
    }
    CommunicationContextHolder.setContext(new CommunicationContext(newCommunicationLog));

    filterChain.doFilter(request, response);

    updateCommunicationLogWithResponseAndObservation(
        newCommunicationLog, contentCachingResponseWrapper, "");

    CommunicationContextHolder.clearContext();
  }

  private HttpCommunicationFromEwpNodeLog createCommunicationLog(
      ZonedDateTime startProcessingDateTime, EwpApiHttpRequestWrapper ewpRequest) {
    HttpCommunicationFromEwpNodeLog newCommunicationLog;
    try {
      newCommunicationLog =
          ewpCommunicationLogService.logCommunicationFromEwpNodeBeforeExecution(
              ewpRequest, startProcessingDateTime, "", null);
    } catch (DomainException | IOException e) {
      throw new IllegalStateException(e);
    }
    return newCommunicationLog;
  }

  private void updateCommunicationLogWithResponseAndObservation(
      HttpCommunicationFromEwpNodeLog communicationLog,
      ContentCachingResponseWrapper response,
      String observations)
      throws IOException {
    if (!ewpCommunicationLogService.updateCommunicationFromEwpNodeAfterExecution(
        communicationLog, response, ZonedDateTime.now(), observations)) {
      throw new IllegalStateException(
          "Failed to update communication log #" + communicationLog.getId() + "with response data");
    }
  }
}
