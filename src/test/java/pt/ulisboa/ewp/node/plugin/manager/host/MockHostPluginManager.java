package pt.ulisboa.ewp.node.plugin.manager.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;

public class MockHostPluginManager extends AbstractHostPluginManager {

  private final Collection<HostPlugin> plugins = new ArrayList<>();
  private final Map<HostPlugin, Collection<HostProvider>> hostProvidersPerPluginMap = new HashMap<>();

  public MockHostPluginManager() {
    super(plugin -> {});
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
  protected <T extends HostProvider> Collection<T> getProvidersOfPlugin(
      HostPlugin hostPlugin, Class<T> providerType) {
    return this.hostProvidersPerPluginMap.getOrDefault(hostPlugin, new ArrayList<>()).stream()
        .filter(p -> providerType.isAssignableFrom(p.getClass()))
        .map(p -> (T) p)
        .collect(Collectors.toList());
  }
}
