package pt.ulisboa.ewp.node.plugin.factory.host;

import org.pf4j.DefaultPluginFactory;
import org.pf4j.Plugin;
import org.pf4j.PluginWrapper;
import org.springframework.core.env.Environment;
import pt.ulisboa.ewp.host.plugin.skeleton.interfaces.PluginPropertiesAware;
import pt.ulisboa.ewp.node.plugin.proxy.DefaultPluginPropertiesProxy;

public class HostPluginFactory extends DefaultPluginFactory {

  private final Environment environment;

  public HostPluginFactory(Environment environment) {
    this.environment = environment;
  }

  @Override
  public Plugin create(PluginWrapper pluginWrapper) {
    Plugin plugin = super.create(pluginWrapper);
    processAwareInterfaces(plugin);
    return plugin;
  }

  private void processAwareInterfaces(Plugin plugin) {
    processPluginPropertiesAwareInterface(plugin);
  }

  private void processPluginPropertiesAwareInterface(Plugin plugin) {
    if (plugin instanceof PluginPropertiesAware) {
      PluginPropertiesAware pluginAware = (PluginPropertiesAware) plugin;
      pluginAware.setPluginPropertiesProxy(
          new DefaultPluginPropertiesProxy(plugin.getWrapper().getPluginId(), environment));
    }
  }
}
