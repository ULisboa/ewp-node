package pt.ulisboa.ewp.node.utils.keystore;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateEncodingException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

public class DecodedKeystore {

  private KeyStore keyStore;
  private String keyStorePassword;

  private Map<String, DecodedCertificateAndKey> decodedCertificateAndKeys;

  public DecodedKeystore(KeyStore keyStore, String keyStorePassword)
      throws KeyStoreException, CertificateEncodingException, UnrecoverableKeyException,
          NoSuchAlgorithmException {
    this.keyStore = keyStore;
    this.keyStorePassword = keyStorePassword;
    initCertificateAndKeys();
  }

  private void initCertificateAndKeys()
      throws KeyStoreException, CertificateEncodingException, UnrecoverableKeyException,
          NoSuchAlgorithmException {
    this.decodedCertificateAndKeys = new HashMap<>();
    Enumeration<String> aliases = keyStore.aliases();
    while (aliases.hasMoreElements()) {
      String alias = aliases.nextElement();
      if (keyStore.getKey(alias, keyStorePassword.toCharArray()) != null) {
        this.decodedCertificateAndKeys.put(
            alias,
            new DecodedCertificateAndKey(
                keyStore.getCertificate(alias),
                keyStore.getKey(alias, keyStorePassword.toCharArray())));
      }
    }
  }

  public KeyStore getKeyStore() {
    return keyStore;
  }

  public String getKeyStorePassword() {
    return keyStorePassword;
  }

  public Map<String, DecodedCertificateAndKey> getDecodedCertificateAndKeys() {
    return decodedCertificateAndKeys;
  }

  public DecodedCertificateAndKey getDecodedCertificateAndKey(String alias) {
    return this.decodedCertificateAndKeys.get(alias);
  }
}
