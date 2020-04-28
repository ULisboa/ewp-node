package pt.ulisboa.ewp.node.api.ewp.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostPrincipal;
import pt.ulisboa.ewp.node.api.ewp.security.exception.EwpApiSecurityException;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.service.security.ewp.HttpSignatureService;
import pt.ulisboa.ewp.node.utils.LoggerUtils;

/**
 * Filter that attempts to authenticate a request by Http Signature. If the filter chain returns a
 * response with unauthorized status code then it fills with the validation error that was raised
 * during the validation.
 */
public class EwpApiHttpSignatureAuthenticationFilter extends OncePerRequestFilter {

  private HttpSignatureService httpSignatureService;

  public EwpApiHttpSignatureAuthenticationFilter(HttpSignatureService httpSignatureService) {
    this.httpSignatureService = httpSignatureService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    EwpApiHttpRequestWrapper ewpApiHttpRequestWrapper = (EwpApiHttpRequestWrapper) request;
    EwpApiAuthenticateMethodResponse result = authenticate(ewpApiHttpRequestWrapper);

    if (result.isRequiredMethodInfoFulfilled()) {
      if (!result.isOk()) {
        throw new EwpApiSecurityException(
            result.getErrorMessage(),
            result.getStatus(),
            EwpApiSecurityException.AuthMethod.HTTPSIG);
      }
      EwpApiHostAuthenticationToken authentication =
          new EwpApiHostAuthenticationToken(
              EwpAuthenticationMethod.HTTP_SIGNATURE,
              new EwpApiHostPrincipal(result.getHeiIdsCoveredByClient()));
      ewpApiHttpRequestWrapper.setAuthenticationToken(authentication);
      SecurityContextHolder.getContext().setAuthentication(authentication);

      LoggerUtils.info(
          "Valid session for host through Http Signature (hei IDs: "
              + authentication.getName()
              + "; roles: "
              + authentication.getAuthorities()
              + ")",
          this.getClass().getCanonicalName());
    }

    chain.doFilter(request, response);

    if (response.getStatus() == HttpStatus.UNAUTHORIZED.value() && result.isUsingMethod()) {
      throw new EwpApiSecurityException(
          result.getErrorMessage(), result.getStatus(), EwpApiSecurityException.AuthMethod.HTTPSIG);
    }
  }

  private EwpApiAuthenticateMethodResponse authenticate(EwpApiHttpRequestWrapper request)
      throws AuthenticationException {
    try {
      return httpSignatureService.verifyHttpSignatureRequest(request);
    } catch (IOException e) {
      LoggerUtils.error(
          "Failed to verify request through Http Signature: " + e.getMessage(),
          EwpApiHttpSignatureAuthenticationFilter.class.getCanonicalName());
      return EwpApiAuthenticateMethodResponse.failureBuilder(
              EwpAuthenticationMethod.HTTP_SIGNATURE,
              "Failed to verify request through HTTP Signature: " + e.getMessage())
          .withResponseCode(HttpStatus.INTERNAL_SERVER_ERROR)
          .build();
    }
  }
}
