package pt.ulisboa.ewp.node.api.admin.security;

import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.util.Collections;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pt.ulisboa.ewp.node.api.common.security.jwt.AbstractJwtTokenAuthenticationFilter;
import pt.ulisboa.ewp.node.api.common.security.jwt.JwtAuthenticationUserDetails;
import pt.ulisboa.ewp.node.api.common.utils.ApiUtils;
import pt.ulisboa.ewp.node.domain.entity.user.UserRole;

/**
 * A filter that authenticates an host, for the Forward EWP APIs. It expects a JWT with the claim
 * "iss" (issuer) filled with the host's code and signed with the host's forward EWP API
 * configuration's secret.
 */
public class AdminApiJwtTokenAuthenticationFilter extends AbstractJwtTokenAuthenticationFilter {

  private AdminApiUserRolesPopulator userRolesPopulator;
  private String secret;

  public AdminApiJwtTokenAuthenticationFilter(
      AuthenticationManager authenticationManager,
      AdminApiUserRolesPopulator userRolesPopulator,
      String secret) {
    super(authenticationManager, true);
    this.userRolesPopulator = userRolesPopulator;
    this.secret = secret;
  }

  @Override
  protected Optional<String> getTokenSecret(DecodedJWT jwt) {
    return Optional.ofNullable(secret);
  }

  @Override
  protected Authentication resolveToAuthentication(DecodedJWT decodedToken) {
    String username = AdminApiSecurityCommonConstants.USER_ADMIN;
    UserRole userRole = userRolesPopulator.getUserRole(username);
    String authority = AdminApiSecurityCommonConstants.ROLE_PREFIX + userRole.name();
    return new AdminApiAuthenticationToken(
        new JwtAuthenticationUserDetails(decodedToken),
        Collections.singletonList(new SimpleGrantedAuthority(authority)));
  }

  @Override
  protected void onUnsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    try {
      ApiUtils.writeResponseBody(
          response,
          HttpServletResponse.SC_UNAUTHORIZED,
          MediaType.APPLICATION_JSON,
          ApiUtils.createApiResponseBody(null));
    } catch (IOException | JAXBException e) {
      logger.error("Failed to write response's body", e);
    }
  }
}
