package pt.ulisboa.ewp.node.service.bootstrap;

import java.io.IOException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import javax.transaction.Transactional;

import org.bouncycastle.operator.OperatorCreationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pt.ulisboa.ewp.node.config.bootstrap.BootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostCoveredHeiBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostForwardEwpApiBootstrapProperties;
import pt.ulisboa.ewp.node.config.bootstrap.HostNotificationApiBootstrapProperties;
import pt.ulisboa.ewp.node.domain.entity.Hei;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.OtherHeiId;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.HostForwardEwpApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.host.notification.HostNotificationApiConfiguration;
import pt.ulisboa.ewp.node.domain.entity.user.UserProfile;
import pt.ulisboa.ewp.node.domain.entity.user.UserRole;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.domain.repository.KeyStoreConfigurationRepository;
import pt.ulisboa.ewp.node.domain.repository.UserProfileRepository;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.keystore.DecodedKeystore;

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
    log.info("Preparing to bootstrap");
    bootstrapEwpHosts();
    bootstrapKeystoreConfiguration();
    bootstrapUserProfiles();
    log.info("Finished bootstrapping");
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
            hostBootstrapProperties.getAdminNotes());

    createHostForwardEwpApiConfiguration(host, hostBootstrapProperties.getForwardEwpApi());
    createHostNotificationApiConfiguration(host, hostBootstrapProperties.getNotificationApi());

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

  private void createHostNotificationApiConfiguration(
      Host host, HostNotificationApiBootstrapProperties notificationApiProperties) {
    HostNotificationApiConfiguration notificationEwpConfiguration =
        HostNotificationApiConfiguration.create(host, notificationApiProperties.getSecret());
    host.setNotificationApiConfiguration(notificationEwpConfiguration);
  }

  private Hei createHei(Host host, HostCoveredHeiBootstrapProperties coveredHeiProperties) {
    Map<Locale, String> localizedName = new HashMap<>();
    coveredHeiProperties
        .getNames()
        .forEach(
            localizedNameProperties -> {
              localizedName.put(
                  Locale.forLanguageTag(localizedNameProperties.getLocale()),
                  localizedNameProperties.getValue());
            });

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

  private void bootstrapKeystoreConfiguration() {
    if (keyStoreConfigurationRepository.getInstance() == null) {
      log.info("Bootstrapping keystore configuration");
      try {
        DecodedKeystore generatedDecodedKeystore = keystoreService.generateKeystore();
        keystoreService.persistKeystore(generatedDecodedKeystore);

      } catch (KeyStoreException
          | OperatorCreationException
          | NoSuchProviderException
          | IOException
          | NoSuchAlgorithmException
          | CertificateException
          | UnrecoverableKeyException e) {
        log.error("Failed to persist generated keystore", e);
        System.exit(1);
      }
    } else {
      log.info(
          "Skipping bootstrap of keystore configuration (keystore configuration was found on database)");
    }
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
