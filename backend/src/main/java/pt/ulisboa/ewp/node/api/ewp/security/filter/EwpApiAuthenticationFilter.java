package pt.ulisboa.ewp.node.api.ewp.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiAuthenticateMethodResponse;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostPrincipal;
import pt.ulisboa.ewp.node.api.ewp.security.exception.EwpApiSecurityException;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapper;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.communication.log.http.ewp.EwpHttpCommunicationLogService;
import pt.ulisboa.ewp.node.service.ewp.security.verifier.request.AbstractRequestAuthenticationMethodVerifier;

/** Filter that authenticates a request against some supported EWP authentication method. */
public class EwpApiAuthenticationFilter extends OncePerRequestFilter {

  private static final Logger LOGGER = LoggerFactory.getLogger(EwpApiAuthenticationFilter.class);

  private final Collection<AbstractRequestAuthenticationMethodVerifier> verifiers;
  private final EwpHttpCommunicationLogService ewpHttpCommunicationLogService;

  public EwpApiAuthenticationFilter(
      Collection<AbstractRequestAuthenticationMethodVerifier> verifiers,
      EwpHttpCommunicationLogService ewpHttpCommunicationLogService) {
    this.verifiers = verifiers;
    this.ewpHttpCommunicationLogService = ewpHttpCommunicationLogService;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {

    EwpApiHttpRequestWrapper ewpApiHttpRequestWrapper = (EwpApiHttpRequestWrapper) request;

    // NOTE: If a request does not require authentication then just continue the chain
    if (!isAuthenticationRequiredForRequest(ewpApiHttpRequestWrapper)) {
      chain.doFilter(request, response);
      return;
    }

    boolean success = false;
    for (AbstractRequestAuthenticationMethodVerifier verifier : verifiers) {
      EwpApiAuthenticateMethodResponse result = verifier.verify(ewpApiHttpRequestWrapper);

      if (result.isUsingMethod()) {
        if (result.isRequiredMethodInfoFulfilled()) {
          if (!result.isOk()) {
            throw new EwpApiSecurityException(
                result.getErrorMessage(), result.getStatus(), verifier.getAuthenticationMethod());
          }

          EwpApiHostAuthenticationToken authentication =
              new EwpApiHostAuthenticationToken(
                  verifier.getAuthenticationMethod(),
                  new EwpApiHostPrincipal(result.getHeiIdsCoveredByClient()));
          ewpApiHttpRequestWrapper.setAuthenticationToken(authentication);
          SecurityContextHolder.getContext().setAuthentication(authentication);

          if (CommunicationContextHolder.getContext().getCurrentCommunicationLog() != null
              && CommunicationContextHolder.getContext().getCurrentCommunicationLog()
                  instanceof HttpCommunicationFromEwpNodeLog) {
            ewpHttpCommunicationLogService.updateCommunicationFromEwpNodeAuthenticationData(
                (HttpCommunicationFromEwpNodeLog)
                    CommunicationContextHolder.getContext().getCurrentCommunicationLog(),
                authentication);
          }

          LOGGER.info(
              "Valid session for host through {} (hei IDs: {}; roles: {})",
              authentication.getAuthenticationMethod(),
              authentication.getName(),
              authentication.getAuthorities());

          success = true;
          break;

        } else {
          throw new EwpApiSecurityException(
              result.getErrorMessage(), result.getStatus(), verifier.getAuthenticationMethod());
        }
      }
    }

    if (success) {
      chain.doFilter(request, response);
    } else {
      response.setStatus(HttpStatus.UNAUTHORIZED.value());
    }
  }

  private boolean isAuthenticationRequiredForRequest(EwpApiHttpRequestWrapper request) {
    return !request.getRequestURI().endsWith("/manifest");
  }
}
