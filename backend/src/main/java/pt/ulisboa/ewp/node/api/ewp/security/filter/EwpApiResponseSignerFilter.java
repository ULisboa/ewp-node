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
import pt.ulisboa.ewp.node.service.ewp.security.signer.response.ResponseAuthenticationSigner;
import pt.ulisboa.ewp.node.utils.LoggerUtils;

/**
 * Filter that signs responses following EWP security algorithms once they are about to be
 * committed.
 */
public class EwpApiResponseSignerFilter extends OncePerRequestFilter {

  private final ResponseAuthenticationSigner signer;

  public EwpApiResponseSignerFilter(
      ResponseAuthenticationSigner signer) {
    this.signer = signer;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    EwpOnCommitedResponseWrapper ewpOnCommitedResponseWrapper =
        new EwpOnCommitedResponseWrapper(signer, request, response);

    try {
      chain.doFilter(request, ewpOnCommitedResponseWrapper);
    } finally {
      ewpOnCommitedResponseWrapper.signResponse(request, ewpOnCommitedResponseWrapper);
    }
  }

  private static class EwpOnCommitedResponseWrapper extends OnCommittedResponseWrapper {

    private final ResponseAuthenticationSigner signer;
    private final HttpServletRequest request;

    /**
     * @param response the response to be wrapped
     */
    public EwpOnCommitedResponseWrapper(
        ResponseAuthenticationSigner signer,
        HttpServletRequest request,
        HttpServletResponse response) {
      super(new ContentCachingResponseWrapper(response));
      this.signer = signer;
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

      signer.sign(request, response);

      try {
        ((ContentCachingResponseWrapper) getResponse()).copyBodyToResponse();
      } catch (IOException e) {
        LoggerUtils.error(
            "Failed to copy body from ContentCachingResponseWrapper to original response: "
                + e.getMessage(),
            EwpApiResponseSignerFilter.class.getCanonicalName());
      }
    }
  }
}
