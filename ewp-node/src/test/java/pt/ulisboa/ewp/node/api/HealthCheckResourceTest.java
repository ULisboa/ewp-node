package pt.ulisboa.ewp.node.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Test;

public class HealthCheckResourceTest extends AbstractResourceTest {

  @Test
  public void testSuccessHealthCheck() throws Exception {
    this.mockMvc.perform(get("/rest/healthcheck")).andExpect(status().isOk());
  }
}
