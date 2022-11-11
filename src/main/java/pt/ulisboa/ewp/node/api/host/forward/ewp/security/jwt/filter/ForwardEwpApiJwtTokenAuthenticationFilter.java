package pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiAuthenticationToken;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiHostPrincipal;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.JwtAuthenticationUserDetails;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

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
  protected void onSuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    ForwardEwpApiAuthenticationToken forwardEwpApiAuthenticationToken =
        (ForwardEwpApiAuthenticationToken) authentication;
    request.setAttribute(
        REQUEST_ATTRIBUTE_HOST_NAME, forwardEwpApiAuthenticationToken.getPrincipal().getHost());
  }

  @Override
  protected void onUnsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    try {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML.toString());
      response.getWriter().write(serialize(ForwardEwpApiResponseUtils.createResponseWithMessages(),
          MediaType.APPLICATION_XML));

    } catch (IOException | JAXBException e) {
      logger.error("Failed to write response's body", e);
    }
  }

  public static String serialize(Object object, MediaType mediaType)
      throws JsonProcessingException, JAXBException {
    if (mediaType.equals(MediaType.APPLICATION_JSON)) {
      return new ObjectMapper().writeValueAsString(object);

    } else if (mediaType.equals(MediaType.APPLICATION_XML)) {
      StringWriter result = new StringWriter();
      ApplicationContextProvider.getApplicationContext()
          .getBean(Jaxb2Marshaller.class)
          .getJaxbContext()
          .createMarshaller()
          .marshal(object, result);
      return result.toString();

    } else {
      throw new IllegalArgumentException("Unsupported media type: " + mediaType);
    }
  }
}
