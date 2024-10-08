package pt.ulisboa.ewp.node.api.host.forward.ewp.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.ServletRequestPathUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
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

  private final RequestMappingHandlerMapping requestMappingHandlerMapping;
  private final HostHttpCommunicationLogService hostCommunicationLogService;

  public ForwardEwpApiRequestFilter(
      RequestMappingHandlerMapping requestMappingHandlerMapping,
      HostHttpCommunicationLogService hostCommunicationLogService) {
    this.requestMappingHandlerMapping = requestMappingHandlerMapping;
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

    Optional<ForwardEwpApiEndpoint> forwardEwpApiEndpointOptional =
        getForwardEwpApiEndpointOfHandlerMethod(request);
    ForwardEwpApiEndpoint forwardEwpApiEndpoint = forwardEwpApiEndpointOptional.orElse(null);

    String targetHeiId = null;
    if (forwardEwpApiEndpoint != null
        && !StringUtils.isEmpty(forwardEwpApiEndpoint.targetHeiIdParameterName())) {
      targetHeiId = request.getParameter(forwardEwpApiEndpoint.targetHeiIdParameterName());
    }

    HttpCommunicationFromHostLog newCommunicationLog =
        createCommunicationLog(
            forwardEwpApiEndpoint, targetHeiId, startProcessingDateTime, requestWrapper);
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
      ForwardEwpApiEndpoint forwardEwpApiEndpoint,
      String targetHeiId,
      ZonedDateTime startProcessingDateTime,
      ContentCachingRequestWrapper requestWrapper) {
    HttpCommunicationFromHostLog newCommunicationLog;
    try {
      newCommunicationLog =
          hostCommunicationLogService.logCommunicationFromHost(
              null,
              null,
              forwardEwpApiEndpoint,
              targetHeiId,
              requestWrapper,
              null,
              startProcessingDateTime,
              null,
              "",
              null);
    } catch (DomainException | IOException e) {
      throw new IllegalStateException(e);
    }
    return newCommunicationLog;
  }

  private Optional<ForwardEwpApiEndpoint> getForwardEwpApiEndpointOfHandlerMethod(
      HttpServletRequest request) {
    try {
      if (!ServletRequestPathUtils.hasParsedRequestPath(request)) {
        ServletRequestPathUtils.parseAndCache(request);
      }
      HandlerExecutionChain handlerExecutionChain =
          requestMappingHandlerMapping.getHandler(request);
      if (handlerExecutionChain == null) {
        return Optional.empty();
      }
      Object handler = handlerExecutionChain.getHandler();
      if (handler instanceof HandlerMethod) {
        HandlerMethod handlerMethod = (HandlerMethod) handler;
        ForwardEwpApiEndpoint apiEndpoint =
            handlerMethod.getMethodAnnotation(ForwardEwpApiEndpoint.class);
        if (apiEndpoint == null) {
          LOG.warn(
              "Found handler method ({}) but could not find annotation @ForwardEwpApiEndpoint",
              handlerMethod);
          return Optional.empty();
        }
        return Optional.of(apiEndpoint);

      } else {
        LOG.warn(
            "Failed to obtain the handler method (URL: {})", request.getRequestURL().toString());
        return Optional.empty();
      }
    } catch (Exception e) {
      LOG.warn(
          "Failed to get Forward EWP API endpoint information for the request (URL: {}): {}",
          request.getRequestURL().toString(),
          e.getMessage());
      return Optional.empty();
    }
  }
}
