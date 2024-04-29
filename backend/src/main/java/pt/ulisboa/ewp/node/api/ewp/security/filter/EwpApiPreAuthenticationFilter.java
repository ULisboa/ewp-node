package pt.ulisboa.ewp.node.api.ewp.security.filter;

import java.io.IOException;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.ulisboa.ewp.node.api.ewp.security.exception.EwpApiSecurityException;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;

/**
 * Filter that prepares the environment for further EWP authentication methods.
 */
public class EwpApiPreAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger LOG = LoggerFactory.getLogger(EwpApiPreAuthenticationFilter.class);

  private final Jaxb2HttpMessageConverter jaxb2HttpMessageConverter;

  public EwpApiPreAuthenticationFilter(Jaxb2HttpMessageConverter jaxb2HttpMessageConverter) {
    this.jaxb2HttpMessageConverter = jaxb2HttpMessageConverter;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    EwpApiHttpRequestWrapper ewpApiHttpRequestWrapper = getEwpApiHttpRequestWrapper(request);

    try {
      chain.doFilter(ewpApiHttpRequestWrapper, response);

      if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Signature realm=\"EWP\"");
        response.addHeader(HttpConstants.HEADER_WANT_DIGEST, "SHA-256");

        writeResponse(
            response,
            EwpApiUtils.createErrorResponseWithUserAndDeveloperMessage(
                null, "No authorization method found in the request"));
      }
    } catch (AuthenticationException exception) {
      fillResponseWithAuthenticationError(response, exception);
    }
  }

  private void fillResponseWithAuthenticationError(
      HttpServletResponse response, AuthenticationException exception) {
    if (exception instanceof EwpApiSecurityException) {
      EwpApiSecurityException ewpApiSecurityException = (EwpApiSecurityException) exception;

      response.setStatus(ewpApiSecurityException.getStatus().value());

      if (ewpApiSecurityException.getAuthMethod() == EwpAuthenticationMethod.HTTP_SIGNATURE) {
        response.addHeader("X-EWP-Verification-Message", exception.getMessage());
      }
    }

    writeResponse(
        response,
        EwpApiUtils.createErrorResponseWithUserAndDeveloperMessage(null, exception.getMessage()));
  }

  private void writeResponse(HttpServletResponse response, Object object) {
    try {
      StreamResult streamResult = new StreamResult(response.getOutputStream());

      HttpHeaders headers = new HttpHeaders();
      headers.set(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);

      response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML_VALUE);

      this.jaxb2HttpMessageConverter.writeToResult(object, headers, streamResult);

    } catch (IOException | TransformerException e) {
      throw new IllegalStateException(e);
    }
  }

  private EwpApiHttpRequestWrapper getEwpApiHttpRequestWrapper(HttpServletRequest request) {
    if (request instanceof EwpApiHttpRequestWrapper) {
      return (EwpApiHttpRequestWrapper) request;
    }

    // NOTE: The request may be already wrapped in some other wrapper.
    // Therefore, use the embedded request.
    if (request instanceof HttpServletRequestWrapper) {
      return getEwpApiHttpRequestWrapper(
          (HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest());
    }

    LOG.warn("Unknown request type, wrapping it: " + request.getClass().getName());
    try {
      return new EwpApiHttpRequestWrapper(request);

    } catch (IOException e) {
      throw new IllegalStateException("Invalid request: " + request, e);
    }
  }
}
