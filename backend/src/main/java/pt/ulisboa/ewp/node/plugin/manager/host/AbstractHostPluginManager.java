package pt.ulisboa.ewp.node.plugin.manager.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.aop.aspectj.annotation.AspectJProxyFactory;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.node.config.plugins.PluginsProperties;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
import pt.ulisboa.ewp.node.plugin.initializer.HostPluginInitializer;
import pt.ulisboa.ewp.node.service.communication.log.aspect.host.plugin.provider.HostPluginProviderAspect;

public abstract class AbstractHostPluginManager implements HostPluginManager {

  private static final Logger LOG = LoggerFactory.getLogger(AbstractHostPluginManager.class);

  private final PluginsProperties pluginsProperties;
  private final HostPluginInitializer initializer;

  private final Collection<HostPluginProviderAspect> hostPluginProviderAspects;

  private final Map<String, Collection<HostPlugin>> heiIdToPluginsMap = new HashMap<>();
  private final Map<String, HostPlugin> heiIdToPrimaryPluginMap = new HashMap<>();

  private final Map<HostPlugin, Collection<HostProvider>> pluginToHostProvidersMap =
      new HashMap<>();

  protected AbstractHostPluginManager(
      PluginsProperties pluginsProperties,
      HostPluginInitializer initializer,
      Collection<HostPluginProviderAspect> hostPluginProviderAspects) {
    this.pluginsProperties = pluginsProperties;
    this.initializer = initializer;
    this.hostPluginProviderAspects = hostPluginProviderAspects;
  }

  @PostConstruct
  final void init() {
    loadPlugins();

    Collection<HostPlugin> plugins = getAllPlugins();
    for (HostPlugin plugin : plugins) {
      initPlugin(plugin);
      registerPlugin(plugin);
    }
  }

  protected abstract void loadPlugins();

  protected abstract Collection<HostPlugin> getAllPlugins();

  protected abstract Collection<HostProvider> getAllProvidersOfPlugin(HostPlugin hostPlugin);

  protected void initPlugin(HostPlugin plugin) {
    this.initializer.init(plugin);
  }

  public void registerPlugin(HostPlugin plugin) {
    for (String heiId : plugin.getCoveredHeiIds()) {
      this.heiIdToPluginsMap.computeIfAbsent(heiId, ignored -> new ArrayList<>());
      this.heiIdToPluginsMap.get(heiId).add(plugin);

      if (plugin.isPrimaryForHeiId(heiId)) {
        if (this.heiIdToPrimaryPluginMap.containsKey(heiId)) {
          throw new IllegalStateException(
              "Multiple plugins are set as primary for HEI ID "
                  + heiId
                  + ": "
                  + this.heiIdToPrimaryPluginMap.get(heiId).getWrapper().getPluginId()
                  + " and "
                  + plugin.getWrapper().getPluginId());
        }
        this.heiIdToPrimaryPluginMap.put(heiId, plugin);
      }
    }

    registerPluginHostProviders(plugin);
  }

  private void registerPluginHostProviders(HostPlugin plugin) {
    Collection<HostProvider> providers = getAllProvidersOfPlugin(plugin);
    for (HostProvider provider : providers) {
      this.pluginToHostProvidersMap.computeIfAbsent(plugin, p -> new ArrayList<>());

      HostProvider providerToAdd;
      if (this.pluginsProperties.getAspects().isEnabled()) {
        AspectJProxyFactory proxyFactory = new AspectJProxyFactory(provider);
        proxyFactory.setProxyTargetClass(true);
        for (HostPluginProviderAspect aspect : this.hostPluginProviderAspects) {
          proxyFactory.addAspect(aspect);
        }

        providerToAdd = proxyFactory.getProxy();

      } else {
        providerToAdd = provider;
      }

      processAwareInterfaces(plugin, providerToAdd);
      this.pluginToHostProvidersMap.get(plugin).add(providerToAdd);
    }
  }

  private void processAwareInterfaces(HostPlugin hostPlugin, HostProvider hostProvider) {
    hostProvider.setPlugin(hostPlugin);
  }

  public <T extends HostProvider> boolean hasHostProvider(
      String heiId, Class<T> providerClassType) {
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
      String heiId, Collection<String> ounitIds, Class<T> providerClassType)
      throws EwpUnknownHeiIdException {

    Map<T, Collection<String>> result = new HashMap<>();
    for (String ounitId : ounitIds) {
      Optional<T> providerOptional = getSingleProvider(heiId, ounitId, providerClassType);
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
   *     provided HEI ID.
   */
  public <T extends HostProvider> Map<T, Collection<String>> getOunitCodesCoveredPerProviderOfHeiId(
      String heiId, Collection<String> ounitCodes, Class<T> providerClassType) {

    Map<T, Collection<String>> result = new HashMap<>();
    for (String ounitCode : ounitCodes) {
      Optional<T> providerOptional =
          getSingleProviderByHeiIdAndOunitCode(heiId, ounitCode, providerClassType);
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

  public <T extends HostProvider> Map<String, Collection<T>> getAllProvidersOfTypePerHeiId(
      Class<T> providerClassType) {
    Map<String, Collection<T>> result = new HashMap<>();
    for (String heiId : this.heiIdToPluginsMap.keySet()) {
      result.computeIfAbsent(heiId, ignored -> new ArrayList<>());
      result.get(heiId).addAll(getAllProvidersOfType(heiId, providerClassType));
    }
    return result;
  }

  public <T extends HostProvider> Collection<T> getAllProvidersOfType(Class<T> providerClassType) {
    return this.heiIdToPluginsMap.values().stream()
        .flatMap(Collection::stream)
        .flatMap(p -> getAllProviders(p, providerClassType).stream())
        .collect(Collectors.toList());
  }

  public <T extends HostProvider> Collection<T> getAllProvidersOfType(
      String heiId, Class<T> providerClassType) {
    this.heiIdToPluginsMap.computeIfAbsent(heiId, ignored -> new ArrayList<>());
    Collection<HostPlugin> plugins = getSortedPlugins(heiId);
    return plugins.stream()
        .flatMap(p -> getAllProviders(p, providerClassType).stream())
        .collect(Collectors.toList());
  }

  public <T extends HostProvider> Optional<T> getSingleProviderByHeiIdAndOunitCode(
      String heiId, String ounitCode, Class<T> providerClassType) {
    Optional<HostPlugin> pluginOptional =
        getSinglePluginCoveringHeiIdAndOunitCode(heiId, ounitCode);
    if (pluginOptional.isEmpty()) {
      return Optional.empty();
    }
    return getSingleProvider(pluginOptional.get(), providerClassType);
  }

  public <T extends HostProvider> Optional<T> getSingleProvider(
      HostPlugin hostPlugin, Class<T> providerClassType) {
    Collection<T> providers = getAllProviders(hostPlugin, providerClassType);
    if (providers.isEmpty()) {
      return Optional.empty();
    }
    if (providers.size() > 1) {
      LOG.warn(
          "Multiple admissible providers of class {} found for the same host plugin with ID {}: {}",
          providerClassType.getSimpleName(),
          hostPlugin.getWrapper().getPluginId(),
          providers.stream().map(p -> p.getClass().getSimpleName()).collect(Collectors.toList()));
    }
    T provider = providers.iterator().next();

    return Optional.ofNullable(provider);
  }

  @Override
  public Optional<HostPlugin> getSingleHostPluginByProvider(Class<?> providerClassType) {
    List<HostPlugin> validHostPlugins =
        this.pluginToHostProvidersMap.entrySet().stream()
            .filter(
                e ->
                    e.getValue().stream()
                        .anyMatch(p -> providerClassType.isAssignableFrom(p.getClass())))
            .map(Map.Entry::getKey)
            .collect(Collectors.toList());
    if (validHostPlugins.isEmpty()) {
      return Optional.empty();
    }
    if (validHostPlugins.size() > 1) {
      LOG.warn(
          "There are several valid plugins with the given provider class type ("
              + providerClassType.getSimpleName()
              + "), returning one...");
    }
    return Optional.of(validHostPlugins.iterator().next());
  }

  private <T extends HostProvider> Collection<T> getAllProviders(
      HostPlugin hostPlugin, Class<T> providerClassType) {
    return this.pluginToHostProvidersMap.getOrDefault(hostPlugin, new ArrayList<>()).stream()
        .filter(p -> providerClassType.isAssignableFrom(p.getClass()))
        .map(providerClassType::cast)
        .collect(Collectors.toSet());
  }

  /**
   * Returns the sorted collection of plugins covering a given HEI ID. The sorting rules are, in
   * order:
   *
   * <p>1. Primary host plugins before non-primary host plugins
   *
   * <p>2. Lexicographical order of the plugin IDs
   */
  private Collection<HostPlugin> getSortedPlugins(String heiId) {
    return this.heiIdToPluginsMap.get(heiId).stream()
        .sorted(
            (x, y) -> {
              if (x.isPrimaryForHeiId(heiId) != y.isPrimaryForHeiId(heiId)) {
                return x.isPrimaryForHeiId(heiId) ? -1 : 1;
              }
              return x.getWrapper().getPluginId().compareTo(y.getWrapper().getPluginId());
            })
        .collect(Collectors.toList());
  }

  private Optional<HostPlugin> getSinglePluginCoveringHeiIdAndOunitId(
      String heiId, @Nullable String ounitId) {
    if (ounitId == null) {
      return getPrimaryPluginCoveringHeiId(heiId);

    } else {
      return getSinglePluginCoveringHeiIdAndSatisfyingCondition(
          heiId, p -> p.getCoveredOunitIdsByHeiId(heiId).contains(ounitId));
    }
  }

  private Optional<HostPlugin> getSinglePluginCoveringHeiIdAndOunitCode(
      String heiId, @Nullable String ounitCode) {
    if (ounitCode == null) {
      return getPrimaryPluginCoveringHeiId(heiId);

    } else {
      return getSinglePluginCoveringHeiIdAndSatisfyingCondition(
          heiId, p -> p.getCoveredOunitCodesByHeiId(heiId).contains(ounitCode));
    }
  }

  private Optional<HostPlugin> getSinglePluginCoveringHeiIdAndSatisfyingCondition(
      String heiId, Predicate<HostPlugin> filter) {
    List<HostPlugin> plugins =
        this.heiIdToPluginsMap.getOrDefault(heiId, new ArrayList<>()).stream()
            .filter(filter)
            .collect(Collectors.toList());
    if (plugins.isEmpty()) {
      return Optional.empty();
    }
    if (plugins.size() > 1) {
      LOG.warn(
          "Multiple admissible plugins found for the same HEI ID {}: {}",
          heiId,
          plugins.stream().map(p -> p.getWrapper().getPluginId()).collect(Collectors.toList()));
    }
    return Optional.ofNullable(plugins.iterator().next());
  }

  private Optional<HostPlugin> getPrimaryPluginCoveringHeiId(String heiId) {
    if (!this.heiIdToPrimaryPluginMap.containsKey(heiId)) {
      return Optional.empty();
    }
    return Optional.ofNullable(this.heiIdToPrimaryPluginMap.get(heiId));
  }
}
