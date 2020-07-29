package pt.ulisboa.ewp.node.service.bootstrap;

import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.security.cert.CertificateException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.transaction.Transactional;
import org.apache.commons.codec.binary.Base64;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.bouncycastle.operator.OperatorCreationException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.embedded.tomcat.TomcatServletWebServerFactory;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;
import pt.ulisboa.ewp.node.utils.ConnectorUtils;
import pt.ulisboa.ewp.node.utils.keystore.DecodedCertificateAndKey;
import pt.ulisboa.ewp.node.utils.keystore.DecodedKeystore;

@Service
@Transactional
public class KeystoreBootstrapService
    implements WebServerFactoryCustomizer<TomcatServletWebServerFactory> {

  @Autowired private Logger log;

  private final KeyStoreService keystoreService;

  public KeystoreBootstrapService(KeyStoreService keystoreService) {
    this.keystoreService = keystoreService;
  }

  public void bootstrap() {
    this.bootstrapKeystoreConfiguration();
  }

  private void bootstrapKeystoreConfiguration() {
    if (!keystoreService.isInitialized()) {
      log.info("Bootstrapping keystore configuration with a self signed certificate");
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

  @Override
  public void customize(TomcatServletWebServerFactory factory) {
    factory.addConnectorCustomizers(
        connector -> {
          try {
            SSLHostConfig sslHostConfig = ConnectorUtils.getOrCreateSSLHostConfig(connector);
            setKeystoreFromSslConfiguration(sslHostConfig);
          } catch (IOException
              | CertificateEncodingException
              | UnrecoverableKeyException
              | NoSuchAlgorithmException
              | KeyStoreException e) {
            log.error("Failed to obtain Tomcat's SSL keystore", e);
          }
        });
  }

  private void setKeystoreFromSslConfiguration(SSLHostConfig sslHostConfig)
      throws IOException, KeyStoreException, CertificateEncodingException,
          UnrecoverableKeyException, NoSuchAlgorithmException {
    Set<SSLHostConfigCertificate> sslHostConfigCertificates = sslHostConfig.getCertificates();
    if (!sslHostConfigCertificates.isEmpty()) {
      SSLHostConfigCertificate sslHostConfigCertificate =
          sslHostConfigCertificates.iterator().next();
      DecodedCertificateAndKey decodedCertificateAndKey =
          getDecodedCertificateAndKey(sslHostConfigCertificate);
      keystoreService.persistKeystoreWithCertificateAndKey(
          decodedCertificateAndKey.getCertificate().getEncoded(),
          Base64.encodeBase64(decodedCertificateAndKey.getPrivateKey().getEncoded()));
      log.info("Persisted keystore configuration using SSL's configuration");
    }
  }

  private DecodedCertificateAndKey getDecodedCertificateAndKey(
      SSLHostConfigCertificate sslHostConfigCertificate)
      throws IOException, KeyStoreException, CertificateEncodingException,
          UnrecoverableKeyException, NoSuchAlgorithmException {
    KeyStore keystore = sslHostConfigCertificate.getCertificateKeystore();
    String certificateAlias = getCertificateAlias(sslHostConfigCertificate);
    String keystorePassword = getKeystorePassword(sslHostConfigCertificate);
    DecodedKeystore decodedKeystore = new DecodedKeystore(keystore, keystorePassword);
    return decodedKeystore.getDecodedCertificateAndKey(certificateAlias);
  }

  private String getCertificateAlias(SSLHostConfigCertificate sslHostConfigCertificate)
      throws IOException, KeyStoreException {
    String certificateAlias = sslHostConfigCertificate.getCertificateKeyAlias();
    if (certificateAlias == null) {
      List<String> keystoreAliases =
          Collections.list(sslHostConfigCertificate.getCertificateKeystore().aliases());
      if (keystoreAliases.size() == 1) {
        certificateAlias = keystoreAliases.get(0);
      } else {
        throw new IllegalArgumentException(
            "Certificate alias to use was not provided through configuration");
      }
    }
    return certificateAlias;
  }

  private String getKeystorePassword(SSLHostConfigCertificate sslHostConfigCertificate) {
    String keystorePassword = sslHostConfigCertificate.getCertificateKeystorePassword();
    if (keystorePassword == null) {
      keystorePassword = sslHostConfigCertificate.getCertificateKeyPassword();
    }
    return keystorePassword;
  }
}
