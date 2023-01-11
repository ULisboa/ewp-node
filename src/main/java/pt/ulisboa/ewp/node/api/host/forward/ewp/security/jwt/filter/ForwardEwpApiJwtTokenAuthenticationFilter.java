package pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.filter;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiClientAuthenticationToken;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiHostClientPrincipal;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.JwtAuthenticationUserDetails;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.repository.host.forward.ewp.client.HostForwardEwpApiClientRepository;
import pt.ulisboa.ewp.node.utils.http.converter.xml.Jaxb2HttpMessageConverter;

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

  private final HostForwardEwpApiClientRepository clientRepository;

  private final Jaxb2HttpMessageConverter jaxb2HttpMessageConverter;

  public ForwardEwpApiJwtTokenAuthenticationFilter(
      AuthenticationManager authenticationManager,
      HostForwardEwpApiClientRepository clientRepository,
      Jaxb2HttpMessageConverter jaxb2HttpMessageConverter) {
    super(authenticationManager, true);
    this.clientRepository = clientRepository;
    this.jaxb2HttpMessageConverter = jaxb2HttpMessageConverter;
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

    } catch (IOException | JAXBException | TransformerException e) {
      logger.error("Failed to write response's body", e);
    }
  }

  public String serialize(Object object, MediaType mediaType)
      throws JsonProcessingException, JAXBException, UnsupportedEncodingException, TransformerException {
    if (mediaType.equals(MediaType.APPLICATION_JSON)) {
      return new ObjectMapper().writeValueAsString(object);

    } else if (mediaType.equals(MediaType.APPLICATION_XML)) {
      ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
      StreamResult result = new StreamResult(byteArrayOutputStream);

      HttpHeaders headers = new HttpHeaders();
      headers.set(HttpHeaders.CONTENT_TYPE, mediaType.toString());

      this.jaxb2HttpMessageConverter.writeToResult(object, headers, result);
      return byteArrayOutputStream.toString("UTF-8");

    } else {
      throw new IllegalArgumentException("Unsupported media type: " + mediaType);
    }
  }
}
