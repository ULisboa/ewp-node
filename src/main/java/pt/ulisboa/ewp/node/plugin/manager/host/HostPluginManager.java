package pt.ulisboa.ewp.node.plugin.manager.host;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
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
import org.springframework.core.env.Environment;
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

  private final Environment environment;

  public HostPluginManager(@Value("${plugins.path}") String pluginsPath, Environment environment) {
    super(Path.of(pluginsPath));
    this.environment = environment;
    init();
  }

  private void init() {
    LOGGER.info("Preparing to load plugins from path: {}", super.pluginsRoot.toAbsolutePath());

    super.pluginFactory = createPluginFactory();

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
              "Multiple plugins are set as primary for HEI ID " + heiId + ": " +
                  this.heiIdToPrimaryPluginMap.get(heiId).getWrapper().getPluginId() + " and "
                  + plugin.getWrapper().getPluginId());
        }
        this.heiIdToPrimaryPluginMap.put(heiId, plugin);
      }
    }
  }

  @Override
  protected PluginFactory createPluginFactory() {
    return new HostPluginFactory(environment);
  }

  public <T extends HostProvider> boolean hasHostProvider(Class<T> providerClassType) {
    return !getAllProvidersOfTypePerHeiId(providerClassType).isEmpty();
  }

  public <T extends HostProvider> boolean hasHostProvider(String heiId,
      Class<T> providerClassType) {
    Collection<T> providers = getAllProvidersOfType(heiId, providerClassType);
    return !providers.isEmpty();
  }

  public <T extends HostProvider> Optional<T> getPrimaryProvider(
      String heiId, Class<T> providerClassType) {
    Optional<HostPlugin> primaryPluginOptional = getPrimaryPluginCoveringHeiId(heiId);
    if (primaryPluginOptional.isEmpty()) {
      return Optional.empty();
    }
    return getSingleProvider(primaryPluginOptional.get(), providerClassType);
  }

  public <T extends HostProvider> Optional<T> getSingleProvider(
      String heiId, String ounitId, Class<T> providerClassType) {
    Optional<HostPlugin> pluginOptional = getSinglePluginCoveringHeiIdAndOunitId(heiId, ounitId);
    if (pluginOptional.isEmpty()) {
      return Optional.empty();
    }
    return getSingleProvider(pluginOptional.get(), providerClassType);
  }

  public <T extends HostProvider> Map<T, Collection<String>> getOunitIdsCoveredPerProviderOfHeiId(
      String heiId,
      Collection<String> ounitIds, Class<T> providerClassType) throws EwpUnknownHeiIdException {

    Map<T, Collection<String>> result = new HashMap<>();
    for (String ounitId : ounitIds) {
      Optional<T> providerOptional = getSingleProviderByHeiIdAndOunitId(heiId, ounitId,
          providerClassType);
      if (providerOptional.isPresent()) {
        T provider = providerOptional.get();
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

    Map<T, Collection<String>> result = new HashMap<>();
    for (String ounitCode : ounitCodes) {
      Optional<T> providerOptional = getSingleProviderByHeiIdAndOunitCode(heiId, ounitCode,
          providerClassType);
      if (providerOptional.isPresent()) {
        T provider = providerOptional.get();
        result.computeIfAbsent(provider, ignored -> new ArrayList<>());
        result.get(provider).add(ounitCode);
      }
    }
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

  public <T> Collection<T> getAllProvidersOfType(Class<T> providerClassType) {
    return this.heiIdToPluginsMap.values().stream().flatMap(Collection::stream)
        .flatMap(p -> getExtensions(p, providerClassType).stream())
        .collect(Collectors.toList());
  }

  public <T> Collection<T> getAllProvidersOfType(String heiId, Class<T> providerClassType) {
    this.heiIdToPluginsMap.computeIfAbsent(heiId, ignored -> new ArrayList<>());
    Collection<HostPlugin> plugins = getSortedPlugins(heiId);
    return plugins.stream()
        .flatMap(p -> getExtensions(p, providerClassType).stream())
        .collect(Collectors.toList());
  }

  public <T> Optional<T> getSingleProviderByHeiIdAndOunitId(String heiId, String ounitId,
      Class<T> providerClassType) {
    Optional<HostPlugin> pluginOptional = getSinglePluginCoveringHeiIdAndOunitId(heiId, ounitId);
    if (pluginOptional.isEmpty()) {
      return Optional.empty();
    }
    return getSingleProvider(pluginOptional.get(), providerClassType);
  }

  public <T> Optional<T> getSingleProviderByHeiIdAndOunitCode(String heiId, String ounitCode,
      Class<T> providerClassType) {
    Optional<HostPlugin> pluginOptional = getSinglePluginCoveringHeiIdAndOunitCode(heiId,
        ounitCode);
    if (pluginOptional.isEmpty()) {
      return Optional.empty();
    }
    return getSingleProvider(pluginOptional.get(), providerClassType);
  }

  public <T> Optional<T> getSingleProvider(HostPlugin hostPlugin, Class<T> providerClassType) {
    Collection<T> providers = getAllProviders(hostPlugin, providerClassType);
    if (providers.isEmpty()) {
      return Optional.empty();
    }
    if (providers.size() > 1) {
      LOGGER.warn(
          "Multiple admissible providers of class {} found for the same host plugin with ID {}: {}",
          providerClassType.getSimpleName(),
          hostPlugin.getWrapper().getPluginId(),
          providers.stream().map(p -> p.getClass().getSimpleName()).collect(Collectors.toList()));
    }
    return Optional.ofNullable(providers.iterator().next());
  }

  private <T> Collection<T> getAllProviders(HostPlugin hostPlugin, Class<T> providerClassType) {
    return getExtensions(hostPlugin, providerClassType);
  }

  /**
   * Returns the sorted collection of plugins covering a given HEI ID. The sorting rules are, in
   * order:
   * <p>
   * 1. Primary host plugins before non-primary host plugins
   * <p>
   * 2. Lexicographical order of the plugin IDs
   */
  private Collection<HostPlugin> getSortedPlugins(String heiId) {
    return this.heiIdToPluginsMap.get(heiId)
        .stream()
        .sorted((x, y) -> {
          if (x.isPrimaryForHeiId(heiId) != y.isPrimaryForHeiId(heiId)) {
            return x.isPrimaryForHeiId(heiId) ? -1 : 1;
          }
          return x.getWrapper().getPluginId().compareTo(y.getWrapper().getPluginId());
        })
        .collect(Collectors.toList());
  }

  private Collection<HostPlugin> getAllPlugins() {
    Class<HostPlugin> classType = HostPlugin.class;
    return getPlugins().stream()
        .filter(p -> classType.isAssignableFrom(p.getPlugin().getClass()))
        .map(p -> classType.cast(p.getPlugin()))
        .collect(Collectors.toList());
  }

  private Optional<HostPlugin> getSinglePluginCoveringHeiIdAndOunitId(String heiId,
      @Nullable String ounitId) {
    if (ounitId == null) {
      return getPrimaryPluginCoveringHeiId(heiId);

    } else {
      return getSinglePluginCoveringHeiIdAndSatisfyingCondition(heiId,
          p -> p.getCoveredOunitIdsByHeiId(heiId).contains(ounitId));
    }
  }

  private Optional<HostPlugin> getSinglePluginCoveringHeiIdAndOunitCode(String heiId,
      @Nullable String ounitCode) {
    if (ounitCode == null) {
      return getPrimaryPluginCoveringHeiId(heiId);

    } else {
      return getSinglePluginCoveringHeiIdAndSatisfyingCondition(heiId,
          p -> p.getCoveredOunitCodesByHeiId(heiId).contains(ounitCode));
    }
  }

  private Optional<HostPlugin> getSinglePluginCoveringHeiIdAndSatisfyingCondition(String heiId,
      Predicate<HostPlugin> filter) {
    List<HostPlugin> plugins = this.heiIdToPluginsMap.getOrDefault(heiId, new ArrayList<>())
        .stream()
        .filter(filter)
        .collect(Collectors.toList());
    if (plugins.isEmpty()) {
      return Optional.empty();
    }
    if (plugins.size() > 1) {
      LOGGER.warn("Multiple admissible plugins found for the same HEI ID {}: {}", heiId,
          plugins.stream().map(p -> p.getWrapper().getPluginId()).collect(
              Collectors.toList()));
    }
    return Optional.ofNullable(plugins.iterator().next());
  }

  private <T> Collection<T> getExtensions(Plugin plugin, Class<T> extensionType) {
    return super.getExtensions(extensionType, plugin.getWrapper().getPluginId());
  }

  private Optional<HostPlugin> getPrimaryPluginCoveringHeiId(String heiId) {
    if (!this.heiIdToPrimaryPluginMap.containsKey(heiId)) {
      return Optional.empty();
    }
    return Optional.ofNullable(this.heiIdToPrimaryPluginMap.get(heiId));
  }
}
