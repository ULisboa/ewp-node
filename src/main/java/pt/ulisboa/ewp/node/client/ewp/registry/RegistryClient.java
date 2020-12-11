package pt.ulisboa.ewp.node.client.ewp.registry;

import eu.erasmuswithoutpaper.registryclient.ApiSearchConditions;
import eu.erasmuswithoutpaper.registryclient.ClientImpl;
import eu.erasmuswithoutpaper.registryclient.ClientImplOptions;
import eu.erasmuswithoutpaper.registryclient.DefaultCatalogueFetcher;
import eu.erasmuswithoutpaper.registryclient.HeiEntry;
import java.security.cert.X509Certificate;
import java.security.interfaces.RSAPublicKey;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.w3c.dom.Element;
import pt.ulisboa.ewp.node.config.registry.RegistryProperties;

@Service
public class RegistryClient {

  private static final Logger logger = LoggerFactory.getLogger(RegistryClient.class);
  private eu.erasmuswithoutpaper.registryclient.RegistryClient client;

  @Autowired private RegistryProperties properties;

  @PostConstruct
  private void loadRegistryClient() {
    try {
      ClientImplOptions options = new ClientImplOptions();
      options.setCatalogueFetcher(new DefaultCatalogueFetcher(properties.getUrl()));
      options.setAutoRefreshing(properties.isAutoRefresh());
      options.setTimeBetweenRetries(properties.getTimeBetweenRetriesInMilliseconds());
      client = new ClientImpl(options);

      client.refresh();
    } catch (eu.erasmuswithoutpaper.registryclient.RegistryClient.RefreshFailureException ex) {
      logger.error("Can't refresh registry client", ex);
    }
  }

  public X509Certificate getCertificateKnownInEwpNetwork(X509Certificate[] certificates) {
    if (certificates == null) {
      return null;
    }

    for (X509Certificate certificate : certificates) {
      if (client.isCertificateKnown(certificate)) {
        return certificate;
      }
    }
    return null;
  }

  public Collection<String> getHeisCoveredByCertificate(X509Certificate certificate) {
    if (certificate != null && client.isCertificateKnown(certificate)) {
      return client.getHeisCoveredByCertificate(certificate);
    }
    return new ArrayList<>();
  }

  public RSAPublicKey findRsaPublicKey(String fingerprint) {
    return client.findRsaPublicKey(fingerprint);
  }

  public RSAPublicKey findClientRsaPublicKey(String fingerprint) {
    RSAPublicKey rsaPublicKey = client.findRsaPublicKey(fingerprint);
    return rsaPublicKey != null && client.isClientKeyKnown(rsaPublicKey) ? rsaPublicKey : null;
  }

  public Collection<String> getHeisCoveredByClientKey(RSAPublicKey rsapk) {
    return client.getHeisCoveredByClientKey(rsapk);
  }

  public Element findApi(ApiSearchConditions conditions) {
    return client.findApi(conditions);
  }

  public Collection<Element> findApis(ApiSearchConditions conditions) {
    return client.findApis(conditions);
  }

  public Collection<String> getAllHeiIds() {
    return client.getAllHeis().stream().map(HeiEntry::getId).collect(Collectors.toList());
  }
}
