package pt.ulisboa.ewp.node.api.actuator.security;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pt.ulisboa.ewp.node.api.AbstractResourceIntegrationTest;

@TestPropertySource(
    properties = {
      "actuator.security.username=actuator",
      "actuator.security.password={bcrypt}$2a$10$HmxuRyMdk5DEAcXg95QrR.NpV5inrl7RMN868bzhWosQhS.J.OnKC" // test123
    })
public class ActuatorSecurityIntegrationTest extends AbstractResourceIntegrationTest {

  @Test
  public void givenInvalidAuthToActuator_shouldFailWith401() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.request(HttpMethod.GET, "/actuator")
                .with(httpBasic("invalid", "invalid")))
        .andExpect(status().is(HttpStatus.UNAUTHORIZED.value()));
  }

  @Test
  public void givenValidAuthToActuator_shouldSucceedWith200() throws Exception {
    mockMvc
        .perform(
            MockMvcRequestBuilders.request(HttpMethod.GET, "/actuator")
                .with(httpBasic("actuator", "test123")))
        .andExpect(status().is(HttpStatus.OK.value()));
  }
}
