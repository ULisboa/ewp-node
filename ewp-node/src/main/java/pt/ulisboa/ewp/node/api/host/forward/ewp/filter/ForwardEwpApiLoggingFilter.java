package pt.ulisboa.ewp.node.api.host.forward.ewp.filter;

import java.io.IOException;
import java.time.ZonedDateTime;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;

import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;

/** Filter that logs incoming Forward EWP API requests. */
public class ForwardEwpApiLoggingFilter extends OncePerRequestFilter {

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
      throws ServletException, IOException {
    if (request.getRequestURI().startsWith(EwpApiConstants.EWP_API_BASE_URI)) {
      ZonedDateTime startProcessingDateTime = ZonedDateTime.now();

      //      EwpApiHttpRequestWrapper ewpRequest = new EwpApiHttpRequestWrapper(request);
      ContentCachingResponseWrapper contentCachingResponseWrapper =
          new ContentCachingResponseWrapper(response);
      //      filterChain.doFilter(ewpRequest, contentCachingResponseWrapper);

      filterChain.doFilter(request, contentCachingResponseWrapper);

      ZonedDateTime endProcessingDateTime = ZonedDateTime.now();
      //      ewpCommunicationLogService.logCommunicationFromEwpNode(
      //          ewpRequest,
      //          contentCachingResponseWrapper,
      //          startProcessingDateTime,
      //          endProcessingDateTime,
      //          null);

      contentCachingResponseWrapper.copyBodyToResponse();
    } else {
      filterChain.doFilter(request, response);
    }
  }
}
