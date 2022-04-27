package pt.ulisboa.ewp.node.api.ewp;

import org.springdoc.core.GroupedOpenApi;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EwpApiConfiguration {

  @Bean
  public GroupedOpenApi ewpOpenApi() {
    String[] packagesToScan = {getClass().getPackage().getName()};
    return GroupedOpenApi.builder().group("ewp").packagesToScan(packagesToScan).build();
  }
}
