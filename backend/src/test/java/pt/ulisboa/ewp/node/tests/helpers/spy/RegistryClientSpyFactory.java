package pt.ulisboa.ewp.node.tests.helpers.spy;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@Configuration
public class RegistryClientSpyFactory {

  @Autowired
  private RegistryClient registryClient;

  @Bean
  @Primary
  public RegistryClient registryClientSpy() {
    return Mockito.spy(registryClient);
  }

}
