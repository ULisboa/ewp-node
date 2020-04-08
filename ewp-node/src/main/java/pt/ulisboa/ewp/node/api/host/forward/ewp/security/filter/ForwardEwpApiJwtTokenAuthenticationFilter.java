package pt.ulisboa.ewp.node.api.host.forward.ewp.security.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import java.io.IOException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import pt.ulisboa.ewp.node.api.common.security.jwt.AbstractJwtTokenAuthenticationFilter;
import pt.ulisboa.ewp.node.api.common.security.jwt.JwtAuthenticationUserDetails;
import pt.ulisboa.ewp.node.api.common.utils.ApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ResultType;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiAuthenticationToken;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiHostPrincipal;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;

/**
 * A filter that authenticates an host, for the Forward EWP APIs. It expects a JWT with the claim
 * "iss" (issuer) filled with the host's code and signed with the host's forward EWP API
 * configuration's secret.
 */
public class ForwardEwpApiJwtTokenAuthenticationFilter
    extends AbstractJwtTokenAuthenticationFilter {

  public static final String REQUEST_ATTRIBUTE_HOST_NAME =
      ForwardEwpApiJwtTokenAuthenticationFilter.class.getPackage().getName() + ".HOST";

  private final HostRepository repository;

  public ForwardEwpApiJwtTokenAuthenticationFilter(
      AuthenticationManager authenticationManager, HostRepository repository) {
    super(authenticationManager, true);
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
  protected ForwardEwpApiAuthenticationToken resolveToAuthentication(DecodedJWT decodedToken) {
    Optional<Host> hostOptional = repository.findByCode(decodedToken.getIssuer());
    assert hostOptional.isPresent();
    Host host = hostOptional.get();

    return new ForwardEwpApiAuthenticationToken(
        new JwtAuthenticationUserDetails(decodedToken), new ForwardEwpApiHostPrincipal(host));
  }

  @Override
  protected void onSuccessfulAuthentication(HttpServletRequest request,
      HttpServletResponse response, Authentication authentication) {
    ForwardEwpApiAuthenticationToken forwardEwpApiAuthenticationToken = (ForwardEwpApiAuthenticationToken) authentication;
    request.setAttribute(REQUEST_ATTRIBUTE_HOST_NAME,
        forwardEwpApiAuthenticationToken.getPrincipal().getHost());
  }

  @Override
  protected void onUnsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    try {
      ApiUtils.writeResponseBody(
          response,
          HttpServletResponse.SC_UNAUTHORIZED,
          MediaType.APPLICATION_XML,
          ForwardEwpApiResponseUtils.createEmptyResponseWithMessages(
              ResultType.REQUEST_AUTHENTICATION_ERROR));
    } catch (IOException | JAXBException e) {
      logger.error("Failed to write response's body", e);
    }
  }
}
