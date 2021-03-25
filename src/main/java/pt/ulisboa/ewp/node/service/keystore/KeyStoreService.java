package pt.ulisboa.ewp.node.service.keystore;

import com.google.common.base.Suppliers;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.SignatureException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;
import org.bouncycastle.operator.OperatorCreationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import pt.ulisboa.ewp.node.config.security.SecurityProperties;
import pt.ulisboa.ewp.node.domain.entity.KeyStoreConfiguration;
import pt.ulisboa.ewp.node.domain.repository.KeyStoreConfigurationRepository;
import pt.ulisboa.ewp.node.exception.keystore.KeysDoNotMatchException;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.CertificateUtils;
import pt.ulisboa.ewp.node.utils.SignatureUtils;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;
import pt.ulisboa.ewp.node.utils.keystore.DecodedKeystore;
import pt.ulisboa.ewp.node.utils.keystore.KeyStoreConstants;
import pt.ulisboa.ewp.node.utils.keystore.KeyStoreGenerator;
import pt.ulisboa.ewp.node.utils.keystore.KeyStoreUtil;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

@Component
@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@Transactional
public class KeyStoreService {

  @Autowired private Logger log;

  @Autowired @Lazy protected MessageResolver messages;

  @Autowired private KeyStoreConfigurationRepository keyStoreConfigurationRepository;

  private SecurityProperties securityProperties;
  private Supplier<DecodedKeystore> decodedKeyStoreSupplier;
  private Supplier<DecodedCertificateAndKey> decodedCertificateAndKeySupplier;

  public KeyStoreService(SecurityProperties securityProperties) {
    this.securityProperties = securityProperties;
    initKeyStoreSuppliers();
  }

  public boolean isInitialized() {
    return keyStoreConfigurationRepository.getInstance() != null;
  }

  private void initKeyStoreSuppliers() {
    this.decodedKeyStoreSupplier =
        Suppliers.memoizeWithExpiration(
            this::loadDecodedKeyStoreFromStorage,
            securityProperties.getKeyStore().getCacheValidityInSeconds(),
            TimeUnit.SECONDS);
    this.decodedCertificateAndKeySupplier =
        Suppliers.memoizeWithExpiration(
            this::loadDecodedCertificateAndKeyFromStorage,
            securityProperties.getKeyStore().getCacheValidityInSeconds(),
            TimeUnit.SECONDS);
  }

  public DecodedKeystore generateKeystore()
      throws CertificateException, NoSuchAlgorithmException, KeyStoreException,
          OperatorCreationException, NoSuchProviderException, IOException,
          UnrecoverableKeyException {
    return KeyStoreGenerator.generate(
        securityProperties.getKeyStore().getPassword(),
        KeyStoreConstants.DEFAULT_CERTIFICATE_ALIAS);
  }

  public boolean persistKeystoreWithCertificateAndKey(
      byte[] certificateBytes, byte[] privateKeyBytes) {
    try {
      X509Certificate certificate = CertificateUtils.parseCertificate(certificateBytes);
      PrivateKey privateKey =
          KeyStoreUtil.parsePrivateKey(certificate.getPublicKey().getAlgorithm(), privateKeyBytes);

      if (!SignatureUtils.verifyKeysMatch(certificate.getPublicKey(), privateKey)) {
        throw new KeysDoNotMatchException();
      }

      DecodedKeystore generatedDecodedKeystore =
          KeyStoreGenerator.generateFromCertificateAndKey(
              securityProperties.getKeyStore().getPassword(),
              KeyStoreConstants.DEFAULT_CERTIFICATE_ALIAS,
              certificate,
              privateKey);
      return persistKeystore(generatedDecodedKeystore);

    } catch (KeysDoNotMatchException e) {
      MessageService.getInstance()
          .add(Severity.ERROR, messages.get("error.certificate.private.key.do.not.match"));
      return false;
    } catch (CertificateException
        | IOException
        | KeyStoreException
        | NoSuchAlgorithmException
        | UnrecoverableKeyException
        | InvalidKeySpecException
        | SignatureException
        | InvalidKeyException e) {
      log.error("Failed to persist keystore", e);
      return false;
    }
  }

  public DecodedCertificateAndKey getDecodedCertificateAndKeyFromStorage() {
    return decodedCertificateAndKeySupplier.get();
  }

  public DecodedKeystore getDecodedKeyStoreFromStorage() {
    return decodedKeyStoreSupplier.get();
  }

  public boolean persistKeystore(DecodedKeystore decodedKeystore)
      throws CertificateException, NoSuchAlgorithmException, KeyStoreException, IOException {
    String certificateAlias =
        decodedKeystore.getDecodedCertificateAndKeys().keySet().iterator().next();
    KeyStoreConfiguration keystoreConfiguration = keyStoreConfigurationRepository.getInstance();

    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    decodedKeystore
        .getKeyStore()
        .store(byteArrayOutputStream, securityProperties.getKeyStore().getPassword().toCharArray());

    if (keystoreConfiguration == null) {
      keystoreConfiguration =
          KeyStoreConfiguration.create(byteArrayOutputStream.toByteArray(), certificateAlias);
    } else {
      keystoreConfiguration.setKeystore(byteArrayOutputStream.toByteArray());
      keystoreConfiguration.setCertificateAlias(certificateAlias);
    }

    return keyStoreConfigurationRepository.persist(keystoreConfiguration);
  }

  private DecodedKeystore loadDecodedKeyStoreFromStorage() {
    try {
      log.info("Loading keystore from storage");
      KeyStoreConfiguration keystoreConfiguration = keyStoreConfigurationRepository.getInstance();
      KeyStore keyStore = getKeystore(keystoreConfiguration);
      return new DecodedKeystore(keyStore, securityProperties.getKeyStore().getPassword());
    } catch (KeyStoreException
        | CertificateException
        | NoSuchAlgorithmException
        | IOException
        | UnrecoverableKeyException e) {
      throw new IllegalStateException("Failed to get and decode stored keystore", e);
    }
  }

  private DecodedCertificateAndKey loadDecodedCertificateAndKeyFromStorage() {
    log.info("Loading certificate and key from storage");
    KeyStoreConfiguration keystoreConfiguration = keyStoreConfigurationRepository.getInstance();
    DecodedKeystore decodedKeyStoreFromStorage = getDecodedKeyStoreFromStorage();
    return decodedKeyStoreFromStorage.getDecodedCertificateAndKey(
        keystoreConfiguration.getCertificateAlias());
  }

  private KeyStore getKeystore(KeyStoreConfiguration keystoreConfiguration)
      throws KeyStoreException, CertificateException, NoSuchAlgorithmException, IOException {
    byte[] keystoreBytes = keystoreConfiguration.getKeystore();
    KeyStore keyStore = KeyStore.getInstance("JKS");
    keyStore.load(
        new ByteArrayInputStream(keystoreBytes),
        securityProperties.getKeyStore().getPassword().toCharArray());
    return keyStore;
  }
}
