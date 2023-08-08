package pt.ulisboa.ewp.node.plugin.manager.host;

import java.util.*;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.node.config.plugins.PluginsAspectsProperties;
import pt.ulisboa.ewp.node.config.plugins.PluginsProperties;

public class MockHostPluginManager extends AbstractHostPluginManager {

  private final Collection<HostPlugin> plugins = new ArrayList<>();
  private final Map<HostPlugin, Collection<HostProvider>> hostProvidersPerPluginMap = new HashMap<>();

  public MockHostPluginManager() {
    super(createPluginsProperties(), plugin -> {}, List.of());
  }

  private static PluginsProperties createPluginsProperties() {
    PluginsAspectsProperties pluginsAspectsProperties = new PluginsAspectsProperties();
    pluginsAspectsProperties.setEnabled(false);

    PluginsProperties pluginsProperties = new PluginsProperties();
    pluginsProperties.setAspects(pluginsAspectsProperties);
    return pluginsProperties;
  }

  public void registerPlugin(HostPlugin plugin, Collection<HostProvider> hostProviders) {
    this.plugins.add(plugin);
    this.hostProvidersPerPluginMap.put(plugin, hostProviders);
    super.registerPlugin(plugin);
  }

  @Override
  protected void loadPlugins() {}

  @Override
  protected Collection<HostPlugin> getAllPlugins() {
    return this.plugins;
  }

  @Override
  protected Collection<HostProvider> getAllProvidersOfPlugin(HostPlugin hostPlugin) {
    return this.hostProvidersPerPluginMap.get(hostPlugin);
  }
}
