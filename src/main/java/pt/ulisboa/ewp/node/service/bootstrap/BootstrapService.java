package pt.ulisboa.ewp.node.service.bootstrap;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import javax.transaction.Transactional;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.config.bootstrap.BootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostCoveredHeiBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostForwardEwpApiBootstrapProperties;
import pt.ulisboa.ewp.node.domain.entity.Hei;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.OtherHeiId;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.HostForwardEwpApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.user.UserProfile;
import pt.ulisboa.ewp.node.domain.entity.user.UserRole;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.domain.repository.KeyStoreConfigurationRepository;
import pt.ulisboa.ewp.node.domain.repository.UserProfileRepository;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;

@Service
@Transactional
public class BootstrapService {

  @Autowired private Logger log;

  @Autowired private BootstrapProperties bootstrapProperties;

  @Autowired private HostRepository hostRepository;

  @Autowired private KeyStoreConfigurationRepository keyStoreConfigurationRepository;

  @Autowired private KeyStoreService keystoreService;

  @Autowired private UserProfileRepository userProfileRepository;

  public void bootstrap() {
    bootstrapEwpHosts();
    bootstrapUserProfiles();
  }

  private void bootstrapEwpHosts() {
    if (hostRepository.findAll().isEmpty()) {
      log.info("Bootstrapping EWP hosts");
      bootstrapProperties
          .getHosts()
          .forEach(
              hostBootstrapProperties ->
                  hostRepository.persist(createHost(hostBootstrapProperties)));
    } else {
      log.info("Skipping bootstrap of EWP hosts (EWP hosts were found on database)");
    }
  }

  private Host createHost(HostBootstrapProperties hostBootstrapProperties) {
    Host host =
        Host.create(
            hostBootstrapProperties.getCode(),
            hostBootstrapProperties.getDescription(),
            hostBootstrapProperties.getAdminEmail(),
            hostBootstrapProperties.getAdminNotes(),
            hostBootstrapProperties.getAdminProvider());

    createHostForwardEwpApiConfiguration(host, hostBootstrapProperties.getForwardEwpApi());

    hostBootstrapProperties
        .getCoveredHeis()
        .forEach(
            coveredHeiProperties ->
                host.getCoveredHeis().add(createHei(host, coveredHeiProperties)));

    return host;
  }

  private void createHostForwardEwpApiConfiguration(
      Host host, HostForwardEwpApiBootstrapProperties forwardEwpApiProperties) {
    HostForwardEwpApiConfiguration forwardEwpApiConfiguration =
        HostForwardEwpApiConfiguration.create(host, forwardEwpApiProperties.getSecret());
    host.setForwardEwpApiConfiguration(forwardEwpApiConfiguration);
  }

  private Hei createHei(Host host, HostCoveredHeiBootstrapProperties coveredHeiProperties) {
    Map<Locale, String> localizedName = new HashMap<>();
    coveredHeiProperties
        .getNames()
        .forEach(
            localizedNameProperties ->
                localizedName.put(
                    Locale.forLanguageTag(localizedNameProperties.getLocale()),
                    localizedNameProperties.getValue()));

    Hei hei = Hei.create(host, coveredHeiProperties.getSchacCode(), localizedName);

    coveredHeiProperties
        .getOtherHeiIds()
        .forEach(
            otherHeiIdProperties -> {
              OtherHeiId otherHeiId =
                  OtherHeiId.create(
                      hei, otherHeiIdProperties.getType(), otherHeiIdProperties.getValue());
              hei.getOtherHeiIds().add(otherHeiId);
            });

    return hei;
  }

  private void bootstrapUserProfiles() {
    if (userProfileRepository.findAll().isEmpty()) {
      log.info("Bootstrapping user profiles");
      bootstrapProperties
          .getUserProfiles()
          .forEach(
              userProfileBootstrapProperties ->
                  userProfileRepository.persist(
                      UserProfile.create(
                          userProfileBootstrapProperties.getUsername(),
                          UserRole.valueOf(userProfileBootstrapProperties.getRole()))));
    } else {
      log.info("Skipping bootstrap of user profiles (user profiles was found on database)");
    }
  }
}
