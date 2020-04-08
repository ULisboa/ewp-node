package pt.ulisboa.ewp.node.api.ewp.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.xml.transform.stream.StreamResult;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.firewall.FirewalledRequest;
import org.springframework.web.filter.OncePerRequestFilter;

import pt.ulisboa.ewp.node.api.ewp.security.exception.EwpApiSecurityException;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.utils.LoggerUtils;
import pt.ulisboa.ewp.node.utils.http.HttpConstants;
import eu.erasmuswithoutpaper.api.architecture.ErrorResponse;
import eu.erasmuswithoutpaper.api.architecture.MultilineString;

/** Filter that prepares the environment for further EWP authentication methods. */
public class EwpApiPreAuthenticationFilter extends OncePerRequestFilter {

  private Jaxb2Marshaller jaxb2Marshaller;

  public EwpApiPreAuthenticationFilter(Jaxb2Marshaller jaxb2Marshaller) {
    this.jaxb2Marshaller = jaxb2Marshaller;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    EwpApiHttpRequestWrapper ewpApiHttpRequestWrapper;
    if (request instanceof HttpServletRequestWrapper) {
      request = (HttpServletRequest) ((HttpServletRequestWrapper) request).getRequest();
    }
    if (request instanceof FirewalledRequest) {
      ewpApiHttpRequestWrapper =
          (EwpApiHttpRequestWrapper) ((FirewalledRequest) request).getRequest();
    } else if (request instanceof EwpApiHttpRequestWrapper) {
      ewpApiHttpRequestWrapper = (EwpApiHttpRequestWrapper) request;
    } else {
      LoggerUtils.warning(
          "Unknown request type, wrapping it: " + request.getClass().getName(),
          this.getClass().getCanonicalName());
      ewpApiHttpRequestWrapper = new EwpApiHttpRequestWrapper(request);
    }

    try {
      chain.doFilter(ewpApiHttpRequestWrapper, response);

      if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
        response.addHeader(HttpHeaders.WWW_AUTHENTICATE, "Signature realm=\"EWP\"");
        response.addHeader(HttpConstants.HEADER_WANT_DIGEST, "SHA-256");

        writeResponseBody(
            response, createErrorResponse("No authorization method found in the request"));
      }
    } catch (AuthenticationException exception) {
      fillResponseWithAuthenticationError(response, exception);
    }
  }

  private void fillResponseWithAuthenticationError(
      HttpServletResponse response, AuthenticationException exception) throws IOException {
    if (exception instanceof EwpApiSecurityException) {
      EwpApiSecurityException ewpApiSecurityException = (EwpApiSecurityException) exception;

      response.setStatus(ewpApiSecurityException.getStatus().value());

      if (ewpApiSecurityException.getAuthMethod() == EwpApiSecurityException.AuthMethod.HTTPSIG) {
        response.addHeader("X-EWP-Verification-Message", exception.getMessage());
      }
    }

    writeResponseBody(response, createErrorResponse(exception.getMessage()));
  }

  private ErrorResponse createErrorResponse(String exceptionMessage) {
    ErrorResponse errorResponse = new ErrorResponse();
    MultilineString message = new MultilineString();
    message.setValue(exceptionMessage);
    errorResponse.setDeveloperMessage(message);
    return errorResponse;
  }

  private void writeResponseBody(HttpServletResponse response, Object object) throws IOException {
    response.setContentType(MediaType.APPLICATION_XML_VALUE);

    StreamResult streamResult = new StreamResult(response.getOutputStream());
    jaxb2Marshaller.marshal(object, streamResult);
  }
}
