package pt.ulisboa.ewp.node.plugin.manager.host;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.ulisboa.ewp.node.plugin.initializer.HostPluginInitializer;

@Configuration
public class HostPluginManagerConfig {

  @Bean
  public AbstractHostPluginManager hostPluginManager(
      @Value("${plugins.path}") String pluginsPath, HostPluginInitializer hostPluginInitializer) {
    return new Pf4jHostPluginManager(pluginsPath, hostPluginInitializer);
  }
}
