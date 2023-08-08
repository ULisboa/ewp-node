package pt.ulisboa.ewp.node.plugin.manager.host;

import java.util.Collection;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import pt.ulisboa.ewp.node.config.plugins.PluginsProperties;
import pt.ulisboa.ewp.node.plugin.initializer.HostPluginInitializer;
import pt.ulisboa.ewp.node.service.communication.log.aspect.host.plugin.provider.HostPluginProviderAspect;

@Configuration
public class HostPluginManagerConfig {

  @Bean
  public AbstractHostPluginManager hostPluginManager(
      PluginsProperties pluginsProperties,
      HostPluginInitializer hostPluginInitializer,
      Pf4jHostPluginManager.CustomDefaultPluginManager customDefaultPluginManager,
      Collection<HostPluginProviderAspect> hostPluginProviderAspects) {
    return new Pf4jHostPluginManager(
        pluginsProperties,
        hostPluginInitializer,
        customDefaultPluginManager,
        hostPluginProviderAspects);
  }
}
