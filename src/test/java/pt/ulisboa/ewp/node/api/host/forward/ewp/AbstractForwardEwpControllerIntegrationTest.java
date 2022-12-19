package pt.ulisboa.ewp.node.api.host.forward.ewp;

import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.springSecurity;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.request.RequestPostProcessor;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import pt.ulisboa.ewp.node.api.AbstractResourceIntegrationTest;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.utils.XmlValidator;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

public abstract class AbstractForwardEwpControllerIntegrationTest extends
    AbstractResourceIntegrationTest {

  @Autowired
  private WebApplicationContext wac;

  @Autowired
  private XmlValidator xmlValidator;

  @Autowired
  private HostRepository hostRepository;

  protected MockMvc mockMvc;

  @BeforeEach
  public void setup() {
    this.mockMvc =
        MockMvcBuilders.webAppContextSetup(this.wac)
            .apply(springSecurity())
            .build();
  }

  protected ResultActions executeRequest(HttpMethod method, String uri, HttpParams bodyParams)
      throws Exception {
    Host host = hostRepository.findAll().iterator().next();

    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.request(method, uri)
            .contentType(MediaType.APPLICATION_FORM_URLENCODED).content(bodyParams.toString())
            .with(jwtTokenRequestProcessor(host));

    return this.mockMvc.perform(requestBuilder).andDo(MockMvcResultHandlers.print());
  }

  protected RequestPostProcessor jwtTokenRequestProcessor(Host host) {
    return request -> {
      String clientId = "client-1";
      String jwtToken = JWT.create()
          .withIssuer(clientId)
          .sign(Algorithm.HMAC256(
              host.getForwardEwpApi().getActiveClientById(clientId).get().getSecret()));

      request.addHeader("Authorization", "Bearer " + jwtToken);
      return request;
    };
  }
}
