package pt.ulisboa.ewp.node.utils.keystore;

import java.security.KeyFactory;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

public class KeyStoreUtil {

  private KeyStoreUtil() {}

  public static void setKeyStoreCertificate(
      KeyStore keyStore,
      String keyStorePassword,
      String certificateAlias,
      X509Certificate certificate,
      PrivateKey privateKey)
      throws KeyStoreException {
    X509Certificate[] certificateChain = new X509Certificate[1];
    certificateChain[0] = certificate;
    keyStore.setEntry(
        certificateAlias,
        new KeyStore.PrivateKeyEntry(privateKey, certificateChain),
        new KeyStore.PasswordProtection(keyStorePassword.toCharArray()));
  }

  public static PrivateKey parsePrivateKey(String algorithm, byte[] privateKeyBytes)
      throws NoSuchAlgorithmException, InvalidKeySpecException {
    String privateKeyString =
        new String(privateKeyBytes).replaceAll("-----.+-----", "").replaceAll("\r|\n", "");
    byte[] decodedPrivateKey = Base64.getDecoder().decode(privateKeyString);
    return KeyFactory.getInstance(algorithm)
        .generatePrivate(new PKCS8EncodedKeySpec(decodedPrivateKey));
  }

  public static boolean isSelfIssued(KeyStore keyStore, X509Certificate certificate)
      throws KeyStoreException {
    return keyStore.getCertificateChain(keyStore.getCertificateAlias(certificate)).length == 1
        && certificate.getIssuerDN().equals(certificate.getSubjectDN());
  }
}
