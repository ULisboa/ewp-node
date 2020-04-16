package pt.ulisboa.ewp.node.api.ewp;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;

@Configuration
public class EwpApiConfiguration {

  @Bean
  public GroupedOpenApi ewpOpenApi() {
    String[] pathsToMatch = {EwpApiConstants.API_BASE_URI + "/**"};
    return GroupedOpenApi.builder().setGroup("ewp").pathsToMatch(pathsToMatch).build();
  }
}
