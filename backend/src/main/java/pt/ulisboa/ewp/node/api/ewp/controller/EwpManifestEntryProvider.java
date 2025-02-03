package pt.ulisboa.ewp.node.api.ewp.controller;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.httpsig.v1.CliauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.httpsig.v1.SrvauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.tlscert.v1.SrvauthTlscertV1;
import eu.erasmuswithoutpaper.api.specs.sec.intro.HttpSecurityOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.HostProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

/**
 * Abstract class for classes that provide manifest entries. It supports both constant manifest
 * entries as well generated ones via a HostProvider.
 */
public abstract class EwpManifestEntryProvider {

  private final Map<Class<? extends HostProvider>, HostProviderToManifestEntryConverter<?>> hostProviderToManifestEntryConverters = new HashMap<>();

  private final HostPluginManager hostPluginManager;
  private final ManifestProperties manifestProperties;

  public EwpManifestEntryProvider(HostPluginManager hostPluginManager,
      ManifestProperties manifestProperties) {
    this.hostPluginManager = hostPluginManager;
    this.manifestProperties = manifestProperties;
  }

  public final Collection<ManifestApiEntryBaseV1> getManifestEntries(String heiId,
      String baseUrl) {
    Collection<ManifestApiEntryBaseV1> manifestEntries = new ArrayList<>();

    manifestEntries.addAll(getManifestEntriesSupportedByHost(heiId, baseUrl));
    manifestEntries.addAll(getExtraManifestEntries(heiId, baseUrl));

    return manifestEntries;
  }

  /**
   * Returns a list of manifest entries for the APIs supported by a given host.
   */
  protected Collection<ManifestApiEntryBaseV1> getManifestEntriesSupportedByHost(String heiId,
      String baseUrl) {
    Collection<ManifestApiEntryBaseV1> manifestEntries = new ArrayList<>();

    Set<Class<? extends HostProvider>> knownHostProviderClasses = this.hostProviderToManifestEntryConverters.keySet();
    Map<Class<? extends HostProvider>, Collection<HostProvider>> classToHostProvidersMap = buildClassToHostProvidersMap(
        heiId, knownHostProviderClasses);

    classToHostProvidersMap.forEach((classType, hostProviders) -> {
      boolean createManifestEntry = true;
      if (this.manifestProperties.getEntries().mustExcludeIfNoPrimaryProviderAvailable()) {
        Optional<?> primaryProviderOptional = this.hostPluginManager.getPrimaryProvider(heiId,
            classType);
        if (primaryProviderOptional.isEmpty()) {
          createManifestEntry = false;
        }
      }

      if (createManifestEntry) {
        Optional<ManifestApiEntryBaseV1> manifestEntryOptional = this
            .toManifestEntry(heiId, baseUrl, hostProviders);
        if (manifestEntryOptional.isPresent()) {
          manifestEntries.add(manifestEntryOptional.get());
        }
      }
    });

    return manifestEntries;
  }

  private Map<Class<? extends HostProvider>, Collection<HostProvider>> buildClassToHostProvidersMap(
      String heiId,
      Set<Class<? extends HostProvider>> knownHostProviderClasses) {
    Map<Class<? extends HostProvider>, Collection<HostProvider>> classToHostProvidersMap = new HashMap<>();
    hostPluginManager.getAllProviders(heiId).forEach(hostProvider -> {
      Optional<Class<? extends HostProvider>> hostProviderClassOptional = this.getHostProviderClassFromKnownClasses(
          hostProvider, knownHostProviderClasses);
      if (hostProviderClassOptional.isPresent()) {
        Class<? extends HostProvider> hostProviderClass = hostProviderClassOptional.get();
        classToHostProvidersMap.computeIfAbsent(hostProviderClass, k -> new ArrayList<>());
        classToHostProvidersMap.get(hostProviderClass).add(hostProvider);
      }
    });
    return classToHostProvidersMap;
  }

  /**
   * Given a host provider, returns the lowest superclass that belongs to the provided set of known
   * classes.
   */
  private Optional<Class<? extends HostProvider>> getHostProviderClassFromKnownClasses(
      HostProvider hostProvider,
      Set<Class<? extends HostProvider>> knownHostProviderClasses) {
    Class<?> currentClass = hostProvider.getClass();
    while (currentClass != null && !knownHostProviderClasses.contains(currentClass)) {
      currentClass = currentClass.getSuperclass();
    }

    if (currentClass != null && HostProvider.class.isAssignableFrom(currentClass)) {
      return Optional.of((Class<? extends HostProvider>) currentClass);
    } else {
      return Optional.empty();
    }
  }

  /**
   * Returns a list of additional manifest entries. This may be overridden to provide additional
   * manifest entries that are not dependent of a host plugin.
   */
  protected Collection<ManifestApiEntryBaseV1> getExtraManifestEntries(String heiId,
      String baseUrl) {
    return Collections.emptyList();
  }

  /**
   * Registers a converter that is able to convert a given host provider class to a manifest entry.
   *
   * @param hostProviderClass
   * @param converter
   * @param <T>
   */
  public <T extends HostProvider> void registerHostProviderToManifestEntryConverter(
      Class<T> hostProviderClass, HostProviderToManifestEntryConverter<T> converter) {
    this.hostProviderToManifestEntryConverters.put(hostProviderClass, converter);
  }

  @SuppressWarnings("unchecked")
  // Providers are type safely registered by registerHostProviderToManifestEntryConverter()
  public <T extends HostProvider> Optional<HostProviderToManifestEntryConverter<T>> getHostProviderToManifestEntryConverter(
      Class<T> hostProviderClass) {
    Class<?> currentClass = hostProviderClass;
    while (currentClass != null) {
      if (this.hostProviderToManifestEntryConverters.containsKey(currentClass)) {
        HostProviderToManifestEntryConverter<T> hostProviderToManifestEntryConverter = (HostProviderToManifestEntryConverter<T>) this.hostProviderToManifestEntryConverters
            .get(currentClass);
        return Optional
            .ofNullable(hostProviderToManifestEntryConverter);
      }
      currentClass = currentClass.getSuperclass();
    }
    return Optional.empty();
  }

  public HttpSecurityOptions getDefaultHttpSecurityOptions() {
    HttpSecurityOptions httpSecurityOptions = new HttpSecurityOptions();

    HttpSecurityOptions.ClientAuthMethods clientAuthMethods =
        new HttpSecurityOptions.ClientAuthMethods();

    clientAuthMethods.getAny().add(new CliauthHttpsigV1());

    httpSecurityOptions.setClientAuthMethods(clientAuthMethods);

    HttpSecurityOptions.ServerAuthMethods serverAuthMethods =
        new HttpSecurityOptions.ServerAuthMethods();

    serverAuthMethods.getAny().add(new SrvauthHttpsigV1());
    serverAuthMethods.getAny().add(new SrvauthTlscertV1());

    httpSecurityOptions.setServerAuthMethods(serverAuthMethods);

    return httpSecurityOptions;
  }

  private <T extends HostProvider> Optional<ManifestApiEntryBaseV1> toManifestEntry(String heiId,
      String baseUrl,
      Collection<T> hostProviders) {
    @SuppressWarnings("unchecked")
    Class<T> hostProviderClass = (Class<T>) hostProviders.iterator().next().getClass();
    Optional<HostProviderToManifestEntryConverter<T>> hostProviderToManifestEntryConverterOptional = getHostProviderToManifestEntryConverter(
        hostProviderClass);
    if (hostProviderToManifestEntryConverterOptional.isEmpty()) {
      return Optional.empty();
    }
    return Optional.of(hostProviderToManifestEntryConverterOptional.get()
        .toManifestEntry(heiId, baseUrl, hostProviders));
  }

  public interface HostProviderToManifestEntryConverter<T extends HostProvider> {

    ManifestApiEntryBaseV1 toManifestEntry(String heiId, String baseUrl,
        Collection<T> hostProviders);

  }

}
