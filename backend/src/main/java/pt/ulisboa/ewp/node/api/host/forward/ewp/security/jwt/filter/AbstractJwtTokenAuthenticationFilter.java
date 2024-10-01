package pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.filter;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.LoggerUtils;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

/**
 * A filter that authenticates a user given the JWT authentication token on header.
 *
 * <p>Token verification is done using HMAC256 algorithm.
 */
public abstract class AbstractJwtTokenAuthenticationFilter extends OncePerRequestFilter {

  private final boolean isTokenRequired;
  private String tokenSecret;

  public AbstractJwtTokenAuthenticationFilter(boolean isTokenRequired, String tokenSecret) {
    this(isTokenRequired);
    this.tokenSecret = tokenSecret;
  }

  public AbstractJwtTokenAuthenticationFilter(boolean isTokenRequired) {
    this.isTokenRequired = isTokenRequired;
  }

  @Override
  protected void doFilterInternal(
      HttpServletRequest request, HttpServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    try {
      String token = getToken(request);
      if (token == null) {
        if (isTokenRequired) {
          throwAuthenticationError("Token was not found on request");
        }
        chain.doFilter(request, response);
        return;
      }

      Authentication authentication = getAuthentication(token);

      LoggerUtils.info(
          "Valid session for user "
              + authentication.getName()
              + " "
              + authentication.getAuthorities(),
          AbstractJwtTokenAuthenticationFilter.class.getCanonicalName());

      SecurityContextHolder.getContext().setAuthentication(authentication);
      onSuccessfulAuthentication(request, response, authentication);
    } catch (AuthenticationException failed) {
      SecurityContextHolder.clearContext();

      onUnsuccessfulAuthentication(request, response, failed);

      return;
    }

    chain.doFilter(request, response);
  }

  @SuppressWarnings("WeakerAccess")
  protected String getToken(HttpServletRequest request) {
    String tokenHeader = request.getHeader(ForwardEwpApiSecurityCommonConstants.HEADER_NAME);

    if (tokenHeader == null
        || !tokenHeader.startsWith(ForwardEwpApiSecurityCommonConstants.BEATER_TOKEN_PREFIX)) {
      return null;
    }

    return tokenHeader.replace(ForwardEwpApiSecurityCommonConstants.BEATER_TOKEN_PREFIX, "");
  }

  protected Authentication getAuthentication(String token) {
    DecodedJWT decodeToken = decodeToken(token);
    if (decodeToken == null) {
      return null;
    }
    return resolveToAuthentication(decodeToken);
  }

  protected DecodedJWT decodeToken(String jwtToken) {
    try {
      DecodedJWT decodedToken = JWT.decode(jwtToken);
      Optional<String> tokenSecretOptional = getTokenSecret(decodedToken);
      if (tokenSecretOptional.isPresent()) {
        return JWT.require(Algorithm.HMAC256(tokenSecretOptional.get().getBytes()))
            .build()
            .verify(jwtToken);
      } else {
        LoggerUtils.error(
            "No token secret found for verification of JWT token: " + jwtToken,
            AbstractJwtTokenAuthenticationFilter.class.getSimpleName());
        throwAuthenticationErrorCode(
            ForwardEwpApiSecurityCommonConstants.ERROR_VERIFICATION_ERROR_CODE);
      }
    } catch (TokenExpiredException e) {
      LoggerUtils.error(
          "Token expired: " + jwtToken, AbstractJwtTokenAuthenticationFilter.class.getSimpleName());
      throwAuthenticationError("Token expired: " + e.getLocalizedMessage());
    } catch (JWTVerificationException e) {
      LoggerUtils.error(
          "Invalid token: " + jwtToken + "(" + e.getMessage() + ")",
          AbstractJwtTokenAuthenticationFilter.class.getSimpleName());
      throwAuthenticationError("Invalid token: " + jwtToken + " (" + e.getLocalizedMessage() + ")");
    } catch (RuntimeException e) {
      LoggerUtils.error(
          String.format("Exception found (token=%s): %s", jwtToken, e.getLocalizedMessage()),
          AbstractJwtTokenAuthenticationFilter.class.getSimpleName());
      throwAuthenticationErrorCode(e.getMessage());
    }
    return null;
  }

  /**
   * Returns an authentication instance corresponding to the decoded (verified) JWT token.
   *
   * @return Authentication instance
   */
  protected abstract Authentication resolveToAuthentication(DecodedJWT decodedToken);

  /**
   * Returns the token secret to verify the JWT. It may be overriden to use the decoded JWT (not
   * verified) in order, for instance, obtain its issuer and, thus obtain the corresponding token
   * secret. The default implementation ignores the provided decoded JWT, returning the token secret
   * passed by constructor.
   *
   * @return Token secret to verify JWT
   */
  protected Optional<String> getTokenSecret(DecodedJWT jwt) {
    return Optional.ofNullable(tokenSecret);
  }

  protected void onSuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, Authentication authResult) {}

  protected void onUnsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {}

  protected void throwAuthenticationErrorCode(String errorCode) throws AuthenticationException {
    throwAuthenticationError(MessageResolver.getInstance().get(errorCode));
  }

  protected void throwAuthenticationError(String error) throws AuthenticationException {
    MessageService.getInstance().add(Severity.ERROR, error);
    throw new AuthenticationServiceException(error);
  }
}
