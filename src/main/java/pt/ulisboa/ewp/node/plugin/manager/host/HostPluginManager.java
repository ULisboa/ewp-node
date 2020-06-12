package pt.ulisboa.ewp.node.plugin.manager.host;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.pf4j.DefaultPluginManager;
import org.pf4j.Plugin;
import org.pf4j.PluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.node.plugin.factory.host.HostPluginFactory;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class HostPluginManager extends DefaultPluginManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(HostPluginManager.class);

  private final Map<String, HostPlugin> heiIdToPluginMap = new HashMap<>();

  public HostPluginManager() {
    init();
  }

  private void init() {
    super.loadPlugins();
    super.startPlugins();

    Collection<HostPlugin> plugins = getPlugins(HostPlugin.class);
    for (HostPlugin plugin : plugins) {
      for (String heiId : plugin.getCoveredHeiIds()) {
        if (this.heiIdToPluginMap.containsKey(heiId)) {
          throw new IllegalStateException(
              "HEI ID "
                  + heiId
                  + " is already registered by another plugin: "
                  + heiIdToPluginMap.get(heiId).getClass().getSimpleName());
        }
        this.heiIdToPluginMap.put(heiId, plugin);
      }
    }
  }

  @Override
  protected PluginFactory createPluginFactory() {
    return new HostPluginFactory();
  }

  public <T extends HostProvider> Map<String, T> getProviderPerHeiId(
      Collection<String> heiIds, Class<T> providerClassType) {
    Map<String, T> heiIdToProviderMap = new HashMap<>();
    heiIds.forEach(
        heiId -> {
          Optional<T> providerOptional = getProvider(heiId, providerClassType);
          providerOptional.ifPresent(t -> heiIdToProviderMap.put(heiId, t));
        });
    return heiIdToProviderMap;
  }

  public <T extends HostProvider> Optional<T> getProvider(
      String heiId, Class<T> providerClassType) {
    Collection<T> extensions = getProviders(heiId, providerClassType);
    if (!extensions.isEmpty() && extensions.size() > 1) {
      LOGGER.warn("Multiple providers detected for HEI ID {}, will use the first one", heiId);
    }
    return extensions.stream().findFirst();
  }

  public <T> Collection<T> getProviders(String heiId, Class<T> providerClassType) {
    if (!heiIdToPluginMap.containsKey(heiId)) {
      return Collections.emptyList();
    }
    HostPlugin plugin = heiIdToPluginMap.get(heiId);
    return getExtensions(plugin, providerClassType);
  }

  public <T extends Plugin> Collection<T> getPlugins(Class<T> classType) {
    return getPlugins().stream()
        .filter(p -> classType.isAssignableFrom(p.getPlugin().getClass()))
        .map(p -> classType.cast(p.getPlugin()))
        .collect(Collectors.toList());
  }

  public <T> Collection<T> getExtensions(Plugin plugin, Class<T> extensionType) {
    return super.getExtensions(extensionType, plugin.getWrapper().getPluginId());
  }
}
