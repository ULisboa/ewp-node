package pt.ulisboa.ewp.node.plugin.manager.host;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;

public interface HostPluginManager {

  <T extends HostProvider> boolean hasHostProvider(String heiId, Class<T> providerClassType);

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
}
