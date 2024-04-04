package pt.ulisboa.ewp.node.client.ewp.registry;

import eu.erasmuswithoutpaper.registryclient.ClientImpl;
import eu.erasmuswithoutpaper.registryclient.ClientImplOptions;
import eu.erasmuswithoutpaper.registryclient.DefaultCatalogueFetcher;
import eu.erasmuswithoutpaper.registryclient.HeiEntry;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.config.registry.RegistryProperties;
import pt.ulisboa.ewp.node.events.ewp.discovery.EwpDiscoveryManifestRequestedEvent;

@Service
public class RegistryClient extends ClientImpl {

  private static final Logger LOG = LoggerFactory.getLogger(RegistryClient.class);

  private static final long SLEEP_TIME_AFTER_MANIFEST_REQUESTED_IN_MS = 3 * 1000L;

  private final Lock handleEwpDiscoveryManifestRequestedEventLock = new ReentrantLock();

  @Autowired
  public RegistryClient(RegistryProperties properties) {
    this(createClientOptions(properties));
  }

  public RegistryClient(ClientImplOptions options) {
    super(options);
  }

  private static ClientImplOptions createClientOptions(RegistryProperties properties) {
    LOG.info("Using registry at: " + properties.getUrl());
    ClientImplOptions options = new ClientImplOptions();
    options.setCatalogueFetcher(new DefaultCatalogueFetcher(properties.getUrl()));
    options.setAutoRefreshing(properties.isAutoRefresh());
    options.setTimeBetweenRetries(properties.getTimeBetweenRetriesInMilliseconds());
    options.setMinTimeBetweenQueries(properties.getMinTimeBetweenQueriesInMilliseconds());
    return options;
  }

  public X509Certificate getCertificateKnownInEwpNetwork(X509Certificate[] certificates) {
    if (certificates == null) {
      return null;
    }

    for (X509Certificate certificate : certificates) {
      if (isCertificateKnown(certificate)) {
        return certificate;
      }
    }
    return null;
  }

  public Collection<String> getHeisCoveredByCertificate(X509Certificate certificate) {
    if (certificate != null && isCertificateKnown(certificate)) {
      return super.getHeisCoveredByCertificate(certificate);
    }
    return new ArrayList<>();
  }

  public RSAPublicKey findClientRsaPublicKey(String fingerprint) {
    RSAPublicKey rsaPublicKey = super.findRsaPublicKey(fingerprint);
    return rsaPublicKey != null && isClientKeyKnown(rsaPublicKey) ? rsaPublicKey : null;
  }

  public Collection<String> getAllHeiIds() {
    return getAllHeis().stream().map(HeiEntry::getId).collect(Collectors.toList());
  }

  @EventListener
  @Async
  public void onEwpDiscoveryManifestRequestedEvent(EwpDiscoveryManifestRequestedEvent event)
      throws RefreshFailureException, InterruptedException {
    // NOTE: ensure that only one thread (event) is handled at a given moment
    this.handleEwpDiscoveryManifestRequestedEventLock.lock();

    try {
      Date registryCacheExpiryDate = super.getExpiryDate();
      boolean scheduleCacheRefresh = false;
      if (registryCacheExpiryDate == null || registryCacheExpiryDate.before(new Date())) {
        LOG.info(
            "Registry's cache is outdated (or not existing) and a manifest request was received, scheduling a refresh attempt");
        scheduleCacheRefresh = true;

      } else if (super.findHei(event.getRequestedHeiId()) == null) {
        LOG.info(
            "Registry's cache does not contain the requested HEI, scheduling a refresh attempt");
        scheduleCacheRefresh = true;
      }

      if (scheduleCacheRefresh) {
        // NOTE: wait some few seconds to ensure that registry's is able to cache the requested
        // manifest
        Thread.sleep(SLEEP_TIME_AFTER_MANIFEST_REQUESTED_IN_MS);
        super.refresh();
      }

    } finally {
      this.handleEwpDiscoveryManifestRequestedEventLock.unlock();
    }
  }
}
