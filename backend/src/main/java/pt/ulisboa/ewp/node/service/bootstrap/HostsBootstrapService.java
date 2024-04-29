package pt.ulisboa.ewp.node.service.bootstrap;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Optional;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

@Service
@Transactional
public class HostsBootstrapService {

  private static final Logger LOG = LoggerFactory.getLogger(HostsBootstrapService.class);

  @Autowired
  private BootstrapProperties bootstrapProperties;

  @Autowired
  private HostRepository hostRepository;


  public void bootstrap() {
    LOG.info("Bootstrapping hosts");
    for (HostBootstrapProperties hostProperties : bootstrapProperties.getHosts()) {
      createOrUpdateHost(hostProperties);
    }
  }

  private Host createOrUpdateHost(HostBootstrapProperties hostBootstrapProperties) {
    Optional<Host> hostOptional = hostRepository.findByCode(hostBootstrapProperties.getCode());
    Host host;
    if (hostOptional.isPresent()) {
      host = hostOptional.get();
      host.update(
          hostBootstrapProperties.getDescription(),
          hostBootstrapProperties.getAdminEmail(),
          hostBootstrapProperties.getAdminNotes(),
          hostBootstrapProperties.getAdminProvider(),
          hostBootstrapProperties.isOunitIdInObjectsRequired(),
          hostBootstrapProperties.getOunitIdInObjectsRequiredErrorMessage());
    } else {
      host =
          Host.create(
              hostBootstrapProperties.getCode(),
              hostBootstrapProperties.getDescription(),
              hostBootstrapProperties.getAdminEmail(),
              hostBootstrapProperties.getAdminNotes(),
              hostBootstrapProperties.getAdminProvider(),
              hostBootstrapProperties.isOunitIdInObjectsRequired(),
              hostBootstrapProperties.getOunitIdInObjectsRequiredErrorMessage());
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

    syncHostForwardEwpApiClients(forwardEwpApi, forwardEwpApiProperties);
  }

  private void syncHostForwardEwpApiClients(HostForwardEwpApi forwardEwpApi,
      HostForwardEwpApiBootstrapProperties hostForwardEwpApiBootstrapProperties) {

    markOldHostForwardEwpApiClientsAsInactive(forwardEwpApi, hostForwardEwpApiBootstrapProperties);

    for (HostForwardEwpApiClientBootstrapProperties clientProperties : hostForwardEwpApiBootstrapProperties.getClients()) {
      createOrUpdateHostForwardEwpApiClient(forwardEwpApi, clientProperties);
    }
  }

  /**
   * Host Forward EWP API clients that no longer are specified on bootstrap properties are marked as
   * inactive with a new random secret.
   */
  private void markOldHostForwardEwpApiClientsAsInactive(HostForwardEwpApi forwardEwpApi,
      HostForwardEwpApiBootstrapProperties hostForwardEwpApiBootstrapProperties) {
    Iterator<HostForwardEwpApiClient> clientIterator = forwardEwpApi.getClients().iterator();
    while (clientIterator.hasNext()) {
      HostForwardEwpApiClient client = clientIterator.next();
      Optional<HostForwardEwpApiClientBootstrapProperties> bootstrapClientProperties = hostForwardEwpApiBootstrapProperties.getClientById(
          client.getId());
      if (bootstrapClientProperties.isEmpty()) {
        client.setActive(false);
      }
    }
  }

  private void createOrUpdateHostForwardEwpApiClient(HostForwardEwpApi forwardEwpApi,
      HostForwardEwpApiClientBootstrapProperties clientProperties) {
    Optional<HostForwardEwpApiClient> existingClientOptional = forwardEwpApi.getClientById(
        clientProperties.getId());
    if (existingClientOptional.isPresent()) {
      HostForwardEwpApiClient client = existingClientOptional.get();
      client.setSecret(clientProperties.getSecret());
      client.setActive(true);
    } else {
      forwardEwpApi.getClients()
          .add(HostForwardEwpApiClient.create(forwardEwpApi, clientProperties.getId(),
              clientProperties.getSecret(), true));
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

    syncOtherHeiIds(hei, coveredHeiProperties);

    host.getCoveredHeis().add(hei);

    return hei;
  }

  private void syncOtherHeiIds(Hei hei,
      HostCoveredHeiBootstrapProperties coveredHeiProperties) {
    deleteOldOtherHeiIds(hei, coveredHeiProperties);
    createOrUpdateOtherHeiIds(hei, coveredHeiProperties);
  }

  private void deleteOldOtherHeiIds(Hei hei,
      HostCoveredHeiBootstrapProperties coveredHeiProperties) {
    Iterator<OtherHeiId> otherHeiIdIterator = hei.getOtherHeiIds().iterator();
    while (otherHeiIdIterator.hasNext()) {
      OtherHeiId otherHeiId = otherHeiIdIterator.next();
      Optional<OtherHeiIdBootstrapProperties> boostrapOtherHeiIdProperties = coveredHeiProperties.getOtherHeiIdByType(
          otherHeiId.getType());
      if (boostrapOtherHeiIdProperties.isEmpty()) {
        otherHeiIdIterator.remove();
      }
    }
  }

  private void createOrUpdateOtherHeiIds(Hei hei,
      HostCoveredHeiBootstrapProperties coveredHeiProperties) {
    for (OtherHeiIdBootstrapProperties otherHeiIdProperties : coveredHeiProperties.getOtherHeiIds()) {
      Optional<OtherHeiId> existingOtherHeiIdOptional = hei.getOtherHeiIdByType(
          otherHeiIdProperties.getType());
      if (existingOtherHeiIdOptional.isPresent()) {
        OtherHeiId existingOtherHeiId = existingOtherHeiIdOptional.get();
        existingOtherHeiId.setValue(otherHeiIdProperties.getValue());
      } else {
        OtherHeiId otherHeiId =
            OtherHeiId.create(
                hei, otherHeiIdProperties.getType(), otherHeiIdProperties.getValue());
        hei.getOtherHeiIds().add(otherHeiId);
      }
    }
  }

}
