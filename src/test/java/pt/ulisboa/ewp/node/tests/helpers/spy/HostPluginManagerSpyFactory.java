package pt.ulisboa.ewp.node.tests.helpers.spy;

import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Configuration
public class HostPluginManagerSpyFactory {

  @Autowired
  private HostPluginManager hostPluginManager;

  @Bean
  @Primary
  public HostPluginManager hostPluginManagerSpy() {
    return Mockito.spy(hostPluginManager);
  }

}
