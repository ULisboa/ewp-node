package pt.ulisboa.ewp.node.api.host.forward.ewp;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;
import pt.ulisboa.ewp.node.api.AbstractResourceIntegrationTest;
import pt.ulisboa.ewp.node.api.host.forward.ewp.filter.ForwardEwpApiRequestFilter;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.service.communication.log.http.host.HostHttpCommunicationLogService;

public class ForwardEwpApiAuthenticationControllerIntegrationTest extends
    AbstractResourceIntegrationTest {

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private HostRepository hostRepository;

  @Autowired private RequestMappingHandlerMapping requestMappingHandlerMapping;

  @Autowired
  private HostHttpCommunicationLogService hostCommunicationLogService;

  private MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.wac)
            .addFilters(
                new ForwardEwpApiRequestFilter(
                    requestMappingHandlerMapping, hostCommunicationLogService))
            .apply(springSecurity())
            .build();
  }

  @Test
  public void testGetAnonymous() throws Exception {
    this.mockMvc
        .perform(get(ForwardEwpApiConstants.API_BASE_URI + "authentication/test"))
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testGetAuthenticatedWithCorrectAuthentication() throws Exception {
    Host host = hostRepository.findByCode("sample-host").get();
    String clientId = "client-1";
    String token =
        JWT.create()
            .withIssuer(clientId)
            .sign(Algorithm.HMAC256(
                host.getForwardEwpApi().getActiveClientById(clientId).get().getSecret()));
    this.mockMvc
        .perform(getRequest(token))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isOk());
  }

  @Test
  public void testEchoGetAuthenticatedAndTwoParametersWithUnknownHostCode() throws Exception {
    String token =
        JWT.create()
            .withIssuer(UUID.randomUUID().toString())
            .sign(Algorithm.HMAC256(UUID.randomUUID().toString()));
    this.mockMvc
        .perform(getRequest(token))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized());
  }

  @Test
  public void testEchoGetAuthenticatedAndTwoParametersWithIncorrectSecret() throws Exception {
    Host host = hostRepository.findByCode("sample-host").get();
    String token =
        JWT.create()
            .withIssuer(host.getCode())
            .sign(Algorithm.HMAC256(UUID.randomUUID().toString()));
    this.mockMvc
        .perform(getRequest(token))
        .andDo(MockMvcResultHandlers.print())
        .andExpect(status().isUnauthorized());
  }

  private MockHttpServletRequestBuilder getRequest(String token) {
    return get(ForwardEwpApiConstants.API_BASE_URI + "authentication/test")
        .header(
            ForwardEwpApiSecurityCommonConstants.HEADER_NAME,
            ForwardEwpApiSecurityCommonConstants.BEATER_TOKEN_PREFIX + token);
  }
}
