package pt.ulisboa.ewp.node.api.host.forward.ewp.security;

import java.io.IOException;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import pt.ulisboa.ewp.node.api.common.security.jwt.AbstractJwtTokenAuthenticationFilter;
import pt.ulisboa.ewp.node.api.common.security.jwt.JwtAuthenticationUserDetails;
import pt.ulisboa.ewp.node.api.common.utils.ApiUtils;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;

import com.auth0.jwt.interfaces.DecodedJWT;

/**
 * A filter that authenticates an host, for the Forward EWP APIs. It expects a JWT with the claim
 * "iss" (issuer) filled with the host's code and signed with the host's forward EWP API
 * configuration's secret.
 */
public class ForwardEwpApiJwtTokenAuthenticationFilter
    extends AbstractJwtTokenAuthenticationFilter {

  private HostRepository repository;

  public ForwardEwpApiJwtTokenAuthenticationFilter(
      AuthenticationManager authenticationManager, HostRepository repository) {
    super(authenticationManager);
    this.repository = repository;
  }

  @Override
  protected Optional<String> getTokenSecret(DecodedJWT jwt) {
    String issuer = jwt.getIssuer();
    Optional<Host> hostOptional = repository.findByCode(issuer);
    if (hostOptional.isPresent()) {
      Host host = hostOptional.get();
      return Optional.of(host.getForwardEwpApiConfiguration().getSecret());
    } else {
      return Optional.empty();
    }
  }

  @Override
  protected Authentication resolveToAuthentication(DecodedJWT decodedToken) {
    Optional<Host> hostOptional = repository.findByCode(decodedToken.getIssuer());
    assert hostOptional.isPresent();
    Host host = hostOptional.get();

    return new ForwardEwpApiAuthenticationToken(
        new JwtAuthenticationUserDetails(decodedToken), new ForwardEwpApiHostPrincipal(host));
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
    } catch (IOException e) {
      e.printStackTrace();
    }
  }
}
