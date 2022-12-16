package pt.ulisboa.ewp.node.service.bootstrap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.config.bootstrap.BootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HeiLocalizedNameBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostCoveredHeiBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostForwardEwpApiBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostForwardEwpApiClientBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.OtherHeiIdBootstrapProperties;
import pt.ulisboa.ewp.node.domain.entity.Hei;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.OtherHeiId;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.HostForwardEwpApi;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.domain.repository.KeyStoreConfigurationRepository;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;

@Service
@Transactional
public class BootstrapService {

  @Autowired
  private Logger log;

  @Autowired
  private BootstrapProperties bootstrapProperties;

  @Autowired
  private HostRepository hostRepository;

  @Autowired
  private KeyStoreConfigurationRepository keyStoreConfigurationRepository;

  @Autowired
  private KeyStoreService keystoreService;

  public void bootstrap() {
    bootstrapEwpHosts();
  }

  private void bootstrapEwpHosts() {
    log.info("Bootstrapping EWP hosts");
    for (HostBootstrapProperties hostProperties : bootstrapProperties.getHosts()) {
      createOrUpdateHost(hostProperties);
    }
  }

  private Host createOrUpdateHost(HostBootstrapProperties hostBootstrapProperties) {
    Optional<Host> hostOptional = hostRepository.findByCode(hostBootstrapProperties.getCode());
    Host host;
    if (hostOptional.isPresent()) {
      host = hostOptional.get();
      host.update(hostBootstrapProperties.getDescription(),
          hostBootstrapProperties.getAdminEmail(),
          hostBootstrapProperties.getAdminNotes(),
          hostBootstrapProperties.getAdminProvider());
    } else {
      host = Host.create(
          hostBootstrapProperties.getCode(),
          hostBootstrapProperties.getDescription(),
          hostBootstrapProperties.getAdminEmail(),
          hostBootstrapProperties.getAdminNotes(),
          hostBootstrapProperties.getAdminProvider());
    }

    createOrUpdateHostForwardEwpApi(host, hostBootstrapProperties.getForwardEwpApi());

    for (HostCoveredHeiBootstrapProperties coveredHeiProperties : hostBootstrapProperties.getCoveredHeis()) {
      createOrUpdateHei(host, coveredHeiProperties);
    }

    hostRepository.persist(host);

    return host;
  }

  private void createOrUpdateHostForwardEwpApi(
      Host host, HostForwardEwpApiBootstrapProperties forwardEwpApiProperties) {
    HostForwardEwpApi forwardEwpApi = host.getForwardEwpApi();
    if (forwardEwpApi == null) {
      host.setForwardEwpApi(HostForwardEwpApi.create(host));
      forwardEwpApi = host.getForwardEwpApi();
    }

    for (HostForwardEwpApiClientBootstrapProperties clientProperties : forwardEwpApiProperties.getClients()) {
      createOrUpdateHostForwardEwpApiClient(forwardEwpApi, clientProperties);
    }
  }

  private void createOrUpdateHostForwardEwpApiClient(HostForwardEwpApi forwardEwpApi,
      HostForwardEwpApiClientBootstrapProperties clientProperties) {
    Optional<HostForwardEwpApiClient> existingClientOptional = forwardEwpApi.getClientById(
        clientProperties.getId());
    if (existingClientOptional.isPresent()) {
      HostForwardEwpApiClient client = existingClientOptional.get();
      client.update(clientProperties.getSecret());
    } else {
      forwardEwpApi.getClients()
          .add(HostForwardEwpApiClient.create(forwardEwpApi, clientProperties.getId(),
              clientProperties.getSecret()));
    }
  }

  private Hei createOrUpdateHei(Host host, HostCoveredHeiBootstrapProperties coveredHeiProperties) {
    Map<Locale, String> localizedName = new HashMap<>();
    for (HeiLocalizedNameBootstrapProperties localizedNameProperties : coveredHeiProperties.getNames()) {
      localizedName.put(
          Locale.forLanguageTag(localizedNameProperties.getLocale()),
          localizedNameProperties.getValue());
    }

    Optional<Hei> heiOptional = host.getCoveredHei(coveredHeiProperties.getSchacCode());
    Hei hei;
    if (heiOptional.isPresent()) {
      hei = heiOptional.get();
      hei.update(localizedName);
    } else {
      hei = Hei.create(host, coveredHeiProperties.getSchacCode(), localizedName);
    }

    createOtherHeiIds(hei, coveredHeiProperties);

    host.getCoveredHeis().add(hei);

    return hei;
  }

  private static void createOtherHeiIds(Hei hei,
      HostCoveredHeiBootstrapProperties coveredHeiProperties) {
    hei.getOtherHeiIds().clear();
    for (OtherHeiIdBootstrapProperties otherHeiIdProperties : coveredHeiProperties.getOtherHeiIds()) {
      OtherHeiId otherHeiId =
          OtherHeiId.create(
              hei, otherHeiIdProperties.getType(), otherHeiIdProperties.getValue());
      hei.getOtherHeiIds().add(otherHeiId);
    }
  }
}
