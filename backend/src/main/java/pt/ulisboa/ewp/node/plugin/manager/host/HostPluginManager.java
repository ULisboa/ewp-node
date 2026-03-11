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

  <T extends HostProvider> boolean hasActiveHostProvider(String heiId, Class<T> providerClassType);

  default <T extends HostProvider> List<T> getPrimaryFollowedByNonPrimaryActiveProviders(
      String heiId, Class<T> providerClassType) {
    List<T> result = new ArrayList<>();
    Optional<T> primaryProviderOptional = getActivePrimaryProvider(heiId, providerClassType);
    if (primaryProviderOptional.isEmpty()) {
      return new ArrayList<>();
    }
    T primaryProvider = primaryProviderOptional.get();
    result.add(primaryProvider);

    Collection<T> nonPrimaryProviders =
        getAllActiveProvidersOfType(heiId, providerClassType).stream()
            .filter(p -> p != primaryProvider)
            .collect(Collectors.toList());
    result.addAll(nonPrimaryProviders);

    return result;
  }

  <T extends HostProvider> Optional<T> getActivePrimaryProvider(
      String heiId, Class<T> providerClassType);

  <T extends HostProvider> Optional<T> getActiveSingleProvider(
      String heiId, String ounitId, Class<T> providerClassType);

  <T extends HostProvider> Collection<T> getAllActiveProvidersOfType(
      String heiId, Class<T> providerClassType);

  <T extends HostProvider> Collection<T> getAllActiveProvidersOfType(Class<T> providerClassType);

  <T extends HostProvider> Map<String, Collection<T>> getAllActiveProvidersOfTypePerHeiId(
      Class<T> providerClassType);

  Collection<HostProvider> getAllActiveProviders(String heiId);

  <T extends HostProvider> Map<T, Collection<String>> getOunitIdsCoveredPerActiveProviderOfHeiId(
      String heiId, Collection<String> ounitIds, Class<T> providerClassType);

  <T extends HostProvider> Map<T, Collection<String>> getOunitCodesCoveredPerActiveProviderOfHeiId(
      String heiId, Collection<String> ounitCodes, Class<T> providerClassType);

  Optional<HostPlugin> getSingleHostPluginByActiveProvider(Class<?> providerClassType);
}
