package pt.ulisboa.ewp.node.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;

public class HealthCheckResourceIntegrationTest extends AbstractResourceIntegrationTest {

  @Test
  public void testSuccessHealthCheck() throws Exception {
    this.mockMvc.perform(get("/api/healthcheck")).andExpect(status().isOk());
  }
}
