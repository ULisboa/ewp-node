package pt.ulisboa.ewp.node.plugin.manager.host;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import pt.ulisboa.ewp.host.plugin.skeleton.HostPlugin;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;

public interface HostPluginManager {

  <T extends HostProvider> boolean hasHostProvider(String heiId, Class<T> providerClassType);

  default <T extends HostProvider> List<T> getPrimaryFollowedByNonPrimaryProviders(String heiId, Class<T> providerClassType) {
    List<T> result = new ArrayList<>();
    Optional<T> primaryProviderOptional = getPrimaryProvider(heiId, providerClassType);
    if (primaryProviderOptional.isEmpty()) {
      return new ArrayList<>();
    }
    T primaryProvider = primaryProviderOptional.get();
    result.add(primaryProvider);

    Collection<T> nonPrimaryProviders = getAllProvidersOfType(heiId, providerClassType).stream()
            .filter(p -> p != primaryProvider)
                .collect(Collectors.toList());
    result.addAll(nonPrimaryProviders);

    return result;
  }

  <T extends HostProvider> Optional<T> getPrimaryProvider(String heiId, Class<T> providerClassType);

  <T extends HostProvider> Optional<T> getSingleProvider(
      String heiId, String ounitId, Class<T> providerClassType);

  <T extends HostProvider> Collection<T> getAllProvidersOfType(
      String heiId, Class<T> providerClassType);

  <T extends HostProvider> Collection<T> getAllProvidersOfType(Class<T> providerClassType);

  <T extends HostProvider> Map<String, Collection<T>> getAllProvidersOfTypePerHeiId(
      Class<T> providerClassType);

  Collection<HostProvider> getAllProviders(String heiId);

  <T extends HostProvider> Map<T, Collection<String>> getOunitIdsCoveredPerProviderOfHeiId(
      String heiId, Collection<String> ounitIds, Class<T> providerClassType);

  <T extends HostProvider> Map<T, Collection<String>> getOunitCodesCoveredPerProviderOfHeiId(
      String heiId, Collection<String> ounitCodes, Class<T> providerClassType);

  Optional<HostPlugin> getSingleHostPluginByProvider(Class<?> providerClassType);
}
