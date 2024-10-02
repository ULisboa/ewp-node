package pt.ulisboa.ewp.node.api.ewp.wrapper;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.UriComponentsBuilder;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.wrapper.EwpApiHttpRequestWrapperIntegrationTest.TestConfig;

@Import(TestConfig.class)
public class EwpApiHttpRequestWrapperIntegrationTest extends AbstractEwpControllerIntegrationTest {

  private static final String PARAMETER_ECHO = "echo";

  @LocalServerPort private int port;

  @Autowired private TestRestTemplate restTemplate;

  @Test
  public void testEchoHttpRequestWithParameters_TwoValues_TwoValuesIsReturnedAsResponse() {
    List<String> expectedReturnedValues = List.of("a1", "b2");
    String url =
        UriComponentsBuilder.fromHttpUrl("https://ewp-node:" + port + "/echo")
            .queryParam("echo", expectedReturnedValues.get(0))
            .queryParam("echo", expectedReturnedValues.get(1))
            .toUriString();
    EchoResponse echoResponseBody = this.restTemplate.getForObject(url, EchoResponse.class);
    assertThat(echoResponseBody.getEcho()).isEqualTo(expectedReturnedValues);
  }

  @Configuration
  static class TestConfig {

    @Bean
    public EchoRestController echoRestController() {
      return new EchoRestController();
    }

    @Bean
    public FilterRegistrationBean<EwpApiHttpRequestWrapperFilter> ewpApiHttpRequestWrapperFilter() {
      FilterRegistrationBean<EwpApiHttpRequestWrapperFilter> registrationBean =
          new FilterRegistrationBean<>();
      registrationBean.setFilter(new EwpApiHttpRequestWrapperFilter());
      registrationBean.addUrlPatterns("/*"); // Apply to all URLs or specify patterns
      return registrationBean;
    }
  }

  @RestController
  static class EchoRestController {

    @RequestMapping(
        path = "/echo",
        method = {RequestMethod.GET, RequestMethod.POST})
    public ResponseEntity<EchoResponse> echoGet(
        @RequestParam(value = PARAMETER_ECHO, defaultValue = "") List<String> echo) {
      return ResponseEntity.ok(new EchoResponse(echo));
    }
  }

  private static class EwpApiHttpRequestWrapperFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
        HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
      EwpApiHttpRequestWrapper ewpRequest = new EwpApiHttpRequestWrapper(request);
      filterChain.doFilter(ewpRequest, response);
    }
  }

  private static class EchoResponse {

    private List<String> echo;

    public EchoResponse() {}

    public EchoResponse(List<String> echo) {
      this.echo = echo;
    }

    public List<String> getEcho() {
      return echo;
    }

    public void setEcho(List<String> echo) {
      this.echo = echo;
    }
  }
}
