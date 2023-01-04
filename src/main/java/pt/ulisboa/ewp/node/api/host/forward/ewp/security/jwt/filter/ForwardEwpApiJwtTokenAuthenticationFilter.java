package pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Optional;
import java.util.regex.Pattern;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiClientAuthenticationToken;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiHostClientPrincipal;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.JwtAuthenticationUserDetails;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.repository.host.forward.ewp.client.HostForwardEwpApiClientRepository;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

/**
 * A filter that authenticates an host, for the Forward EWP APIs. It expects a JWT with the claim
 * "iss" (issuer) filled with the host's code and signed with the host's forward EWP API
 * configuration's secret.
 */
public class ForwardEwpApiJwtTokenAuthenticationFilter
    extends AbstractJwtTokenAuthenticationFilter {

  public static final String REQUEST_ATTRIBUTE_HOST_FORWARD_EWP_API_CLIENT_NAME =
      ForwardEwpApiJwtTokenAuthenticationFilter.class.getPackage().getName()
          + ".HOST_FORWARD_EWP_API_CLIENT";

  private static final Pattern PATTERN_ISSUER = Pattern.compile("([^:]+):(.+)");

  private final HostForwardEwpApiClientRepository clientRepository;

  public ForwardEwpApiJwtTokenAuthenticationFilter(
      AuthenticationManager authenticationManager,
      HostForwardEwpApiClientRepository clientRepository) {
    super(authenticationManager, true);
    this.clientRepository = clientRepository;
  }

  @Override
  protected Optional<String> getTokenSecret(DecodedJWT jwt) {
    String issuer = jwt.getIssuer();
    Optional<HostForwardEwpApiClient> hostForwardEwpApiClientOptional = clientRepository.findByIdAndActive(
        issuer, true);
    if (hostForwardEwpApiClientOptional.isPresent()) {
      HostForwardEwpApiClient hostForwardEwpApiClient = hostForwardEwpApiClientOptional.get();
      return Optional.ofNullable(hostForwardEwpApiClient.getSecret());
    } else {
      return Optional.empty();
    }
  }

  @Override
  protected ForwardEwpApiClientAuthenticationToken resolveToAuthentication(
      DecodedJWT decodedToken) {
    Optional<HostForwardEwpApiClient> hostForwardEwpApiClientOptional = clientRepository.findByIdAndActive(
        decodedToken.getIssuer(), true);
    assert hostForwardEwpApiClientOptional.isPresent();
    HostForwardEwpApiClient hostForwardEwpApiClient = hostForwardEwpApiClientOptional.get();

    return new ForwardEwpApiClientAuthenticationToken(
        new JwtAuthenticationUserDetails(decodedToken),
        new ForwardEwpApiHostClientPrincipal(hostForwardEwpApiClient));
  }

  @Override
  protected void onSuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
    ForwardEwpApiClientAuthenticationToken forwardEwpApiClientAuthenticationToken =
        (ForwardEwpApiClientAuthenticationToken) authentication;
    request.setAttribute(
        REQUEST_ATTRIBUTE_HOST_FORWARD_EWP_API_CLIENT_NAME,
        forwardEwpApiClientAuthenticationToken.getPrincipal().getHostForwardEwpApiClient());
  }

  @Override
  protected void onUnsuccessfulAuthentication(
      HttpServletRequest request, HttpServletResponse response, AuthenticationException failed) {
    try {
      response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
      response.addHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_XML.toString());
      response.getWriter()
          .write(serialize(ForwardEwpApiResponseUtils.createResponseWithMessages(),
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
