package pt.ulisboa.ewp.node.plugin.manager.host;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.pf4j.DefaultPluginManager;
import org.pf4j.Plugin;
import org.pf4j.PluginFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
import pt.ulisboa.ewp.node.plugin.factory.host.HostPluginFactory;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
public class HostPluginManager extends DefaultPluginManager {

  private static final Logger LOGGER = LoggerFactory.getLogger(HostPluginManager.class);

  private final Map<String, Collection<HostPlugin>> heiIdToPluginsMap = new HashMap<>();
  private final Map<String, HostPlugin> heiIdToPrimaryPluginMap = new HashMap<>();

  public HostPluginManager(@Value("${plugins.path}") String pluginsPath) {
    super(Path.of(pluginsPath));
    LOGGER.info("Preparing to load plugins from path: {}", super.pluginsRoot.toAbsolutePath());
    init();
  }

  private void init() {
    super.loadPlugins();
    super.startPlugins();

    getAllPlugins().forEach(this::registerPlugin);
  }

  public void registerPlugin(HostPlugin plugin) {
    for (String heiId : plugin.getCoveredHeiIds()) {
      this.heiIdToPluginsMap.computeIfAbsent(heiId, ignored -> new ArrayList<>());
      this.heiIdToPluginsMap.get(heiId).add(plugin);

      if (plugin.isPrimaryForHeiId(heiId)) {
        if (this.heiIdToPrimaryPluginMap.containsKey(heiId)) {
          throw new IllegalStateException(
              "Multiple plugins are set as primary for HEI ID: " + heiId);
        }
        this.heiIdToPrimaryPluginMap.put(heiId, plugin);
      }
    }
  }

  @Override
  protected PluginFactory createPluginFactory() {
    return new HostPluginFactory();
  }

  public <T extends HostProvider> boolean hasHostProvider(String heiId,
      Class<T> providerClassType) {
    Collection<T> providers = getAllProvidersOfType(heiId, providerClassType);
    return !providers.isEmpty();
  }

  public <T extends HostProvider> Optional<T> getProvider(
      String heiId, Class<T> providerClassType) {
    return getProvider(heiId, null, providerClassType);
  }

  public <T extends HostProvider> Optional<T> getProvider(
      String heiId, String ounitId, Class<T> providerClassType) {
    Collection<T> extensions = getProvidersByHeiIdAndOunitId(heiId, ounitId, providerClassType);
    if (!extensions.isEmpty() && extensions.size() > 1) {
      LOGGER.warn(
          "Multiple providers detected for HEI ID {} and OUNIT ID {}, will use the first one",
          heiId, ounitId);
    }
    return extensions.stream().findFirst();
  }

  public <T extends HostProvider> Map<T, Collection<String>> getOunitIdsCoveredPerProviderOfHeiId(
      String heiId,
      Collection<String> ounitIds, Class<T> providerClassType) throws EwpUnknownHeiIdException {

    if (!hasHostProvider(heiId, providerClassType)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    Map<T, Collection<String>> result = new HashMap<>();
    for (String ounitId : ounitIds) {
      Collection<T> providers = getProvidersByHeiIdAndOunitId(heiId, ounitId, providerClassType);
      if (!providers.isEmpty()) {
        T provider = providers.iterator().next();
        result.computeIfAbsent(provider, ignored -> new ArrayList<>());
        result.get(provider).add(ounitId);
      }
    }
    return result;
  }

  /**
   * @throws EwpUnknownHeiIdException Thrown if there is no host provider of given type for the
   *                                  provided HEI ID.
   */
  public <T extends HostProvider> Map<T, Collection<String>> getOunitCodesCoveredPerProviderOfHeiId(
      String heiId,
      Collection<String> ounitCodes, Class<T> providerClassType) {

    if (!hasHostProvider(heiId, providerClassType)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    Map<T, Collection<String>> result = new HashMap<>();
    for (String ounitCode : ounitCodes) {
      Collection<T> providers = getProvidersByHeiIdAndOunitCode(heiId, ounitCode,
          providerClassType);
      if (!providers.isEmpty()) {
        T provider = providers.iterator().next();
        result.computeIfAbsent(provider, ignored -> new ArrayList<>());
        result.get(provider).add(ounitCode);
      }
    }
    return result;
  }

  public Map<Class<?>, Collection<HostProvider>> getAllProvidersPerClassType(String heiId) {
    Map<Class<?>, Collection<HostProvider>> result = new HashMap<>();
    getAllProvidersOfType(heiId, HostProvider.class).forEach(hostProvider -> {
      result.computeIfAbsent(hostProvider.getClass(), ignored -> new ArrayList<>());
      result.get(hostProvider.getClass()).add(hostProvider);
    });
    return result;
  }

  public Collection<HostProvider> getAllProviders(String heiId) {
    return getAllProvidersOfType(heiId, HostProvider.class);
  }

  public <T> Map<String, Collection<T>> getAllProvidersOfTypePerHeiId(Class<T> providerClassType) {
    Map<String, Collection<T>> result = new HashMap<>();
    for (String heiId : this.heiIdToPluginsMap.keySet()) {
      result.computeIfAbsent(heiId, ignored -> new ArrayList<>());
      result.get(heiId).addAll(getAllProvidersOfType(heiId, providerClassType));
    }
    return result;
  }

  public <T> Collection<T> getAllProvidersOfType(String heiId, Class<T> providerClassType) {
    this.heiIdToPluginsMap.computeIfAbsent(heiId, ignored -> new ArrayList<>());
    Collection<HostPlugin> plugins = this.heiIdToPluginsMap.get(heiId);
    return plugins.stream()
        .flatMap(p -> getExtensions(p, providerClassType).stream())
        .collect(Collectors.toList());
  }

  public <T> Collection<T> getProvidersByHeiIdAndOunitId(String heiId, String ounitId,
      Class<T> providerClassType) {
    Optional<HostPlugin> pluginOptional = getAnyPluginForHeiIdAndOunitId(heiId, ounitId);
    if (pluginOptional.isEmpty()) {
      return Collections.emptyList();
    }
    return getExtensions(pluginOptional.get(), providerClassType);
  }

  public <T> Collection<T> getProvidersByHeiIdAndOunitCode(String heiId, String ounitCode,
      Class<T> providerClassType) {
    Optional<HostPlugin> pluginOptional = getAnyPluginForHeiIdAndOunitCode(heiId, ounitCode);
    if (pluginOptional.isEmpty()) {
      return Collections.emptyList();
    }
    return getExtensions(pluginOptional.get(), providerClassType);
  }

  private Collection<HostPlugin> getAllPlugins() {
    Class<HostPlugin> classType = HostPlugin.class;
    return getPlugins().stream()
        .filter(p -> classType.isAssignableFrom(p.getPlugin().getClass()))
        .map(p -> classType.cast(p.getPlugin()))
        .collect(Collectors.toList());
  }

  private <T> Collection<T> getExtensions(Plugin plugin, Class<T> extensionType) {
    return super.getExtensions(extensionType, plugin.getWrapper().getPluginId());
  }

  private Optional<HostPlugin> getAnyPluginForHeiIdAndOunitId(String heiId,
      @Nullable String ounitId) {
    if (ounitId == null) {
      return Optional.ofNullable(this.heiIdToPrimaryPluginMap.get(heiId));

    } else {
      for (HostPlugin hostPlugin : this.heiIdToPluginsMap.getOrDefault(heiId, new ArrayList<>())) {
        if (hostPlugin.getCoveredOunitIdsByHeiId(heiId).contains(ounitId)) {
          return Optional.of(hostPlugin);
        }
      }
      return Optional.empty();
    }
  }

  private Optional<HostPlugin> getAnyPluginForHeiIdAndOunitCode(String heiId,
      @Nullable String ounitCode) {
    if (ounitCode == null) {
      return Optional.ofNullable(this.heiIdToPrimaryPluginMap.get(heiId));

    } else {
      for (HostPlugin hostPlugin : this.heiIdToPluginsMap.getOrDefault(heiId, new ArrayList<>())) {
        if (hostPlugin.getCoveredOunitCodesByHeiId(heiId).contains(ounitCode)) {
          return Optional.of(hostPlugin);
        }
      }
      return Optional.empty();
    }
  }
}
