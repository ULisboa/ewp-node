package pt.ulisboa.ewp.node.plugin.manager.host;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.ulisboa.ewp.node.config.plugins.PluginsProperties;
import pt.ulisboa.ewp.node.plugin.initializer.HostPluginInitializer;

@Configuration
public class HostPluginManagerConfig {

  @Bean
  public AbstractHostPluginManager hostPluginManager(
      PluginsProperties pluginsProperties,
      HostPluginInitializer hostPluginInitializer,
      Pf4jHostPluginManager.CustomDefaultPluginManager customDefaultPluginManager) {
    return new Pf4jHostPluginManager(pluginsProperties, hostPluginInitializer, customDefaultPluginManager);
  }
}
