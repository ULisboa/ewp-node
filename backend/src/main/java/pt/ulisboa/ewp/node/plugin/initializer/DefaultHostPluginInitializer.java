package pt.ulisboa.ewp.node.plugin.initializer;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.node.plugin.proxy.DefaultPluginPropertiesProxy;

/** Class that initializes plugins. Namely, it configures interfaces of the plugins. */
@Component
public class DefaultHostPluginInitializer implements HostPluginInitializer {

  private final Environment environment;

  public DefaultHostPluginInitializer(Environment environment) {
    this.environment = environment;
  }

  public void init(HostPlugin plugin) {
    processAwareInterfaces(plugin);
  }

  private void processAwareInterfaces(HostPlugin plugin) {
    processPluginPropertiesAwareInterface(plugin);
  }

  private void processPluginPropertiesAwareInterface(HostPlugin plugin) {
    plugin.setPluginPropertiesProxy(
        new DefaultPluginPropertiesProxy(plugin.getWrapper().getPluginId(), environment));
  }
}
