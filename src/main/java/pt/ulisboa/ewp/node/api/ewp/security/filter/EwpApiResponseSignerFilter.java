package pt.ulisboa.ewp.node.api.ewp.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.security.web.util.OnCommittedResponseWrapper;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.ContentCachingResponseWrapper;
import org.springframework.web.util.WebUtils;
import pt.ulisboa.ewp.node.service.security.ewp.HttpSignatureService;
import pt.ulisboa.ewp.node.utils.LoggerUtils;

/**
 * Filter that signs responses following EWP security algorithms once they are about to be
 * committed.
 */
public class EwpApiResponseSignerFilter extends OncePerRequestFilter {

  private HttpSignatureService httpSignatureService;

  public EwpApiResponseSignerFilter(HttpSignatureService httpSignatureService) {
    this.httpSignatureService = httpSignatureService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    EwpOnCommitedResponseWrapper ewpOnCommitedResponseWrapper =
        new EwpOnCommitedResponseWrapper(httpSignatureService, request, response);

    try {
      chain.doFilter(request, ewpOnCommitedResponseWrapper);
    } finally {
      ewpOnCommitedResponseWrapper.signResponse(request, ewpOnCommitedResponseWrapper);
    }
  }

  private static class EwpOnCommitedResponseWrapper extends OnCommittedResponseWrapper {

    private HttpSignatureService httpSignatureService;
    private HttpServletRequest request;

    /** @param response the response to be wrapped */
    public EwpOnCommitedResponseWrapper(
        HttpSignatureService httpSignatureService,
        HttpServletRequest request,
        HttpServletResponse response) {
      super(new ContentCachingResponseWrapper(response));
      this.httpSignatureService = httpSignatureService;
      this.request = request;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
      return getResponse().getOutputStream();
    }

    @Override
    protected void onResponseCommitted() {
      signResponse(request, (HttpServletResponse) getResponse());
    }

    private void signResponse(HttpServletRequest request, HttpServletResponse response) {
      if (isDisableOnResponseCommitted()) {
        return;
      }

      byte[] bodyBytes = getResponseData(response);

      if (httpSignatureService.clientWantsSignedResponse(request)) {
        httpSignatureService.signResponse(request, response, bodyBytes);
      }

      try {
        ((ContentCachingResponseWrapper) getResponse()).copyBodyToResponse();
      } catch (IOException e) {
        LoggerUtils.error(
            "Failed to copy body from ContentCachingResponseWrapper to original response: "
                + e.getMessage(),
            EwpApiResponseSignerFilter.class.getCanonicalName());
      }
    }

    private static byte[] getResponseData(HttpServletResponse response) {
      ContentCachingResponseWrapper wrapper =
          WebUtils.getNativeResponse(response, ContentCachingResponseWrapper.class);
      if (wrapper != null) {
        byte[] bodyBytes = wrapper.getContentAsByteArray();
        if (bodyBytes.length > 0) {
          return bodyBytes;
        }
      }
      return new byte[0];
    }
  }
}
