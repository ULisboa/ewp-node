package pt.ulisboa.ewp.node.utils;

import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.UnrecoverableKeyException;
import java.security.cert.X509Certificate;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;
import pt.ulisboa.ewp.node.utils.keystore.KeyStoreUtil;

public class SecurityUtils {

  private SecurityUtils() {
  }

  public static SSLContext createSecurityContext(
      KeyStore keyStore, KeyStore trustStore, String password)
      throws NoSuchProviderException, NoSuchAlgorithmException, UnrecoverableKeyException,
      KeyStoreException, KeyManagementException {
    KeyManager[] keyManagers = null;
    if (!KeyStoreUtil.isSelfIssued(
        keyStore, (X509Certificate) keyStore.getCertificate(keyStore.aliases().nextElement()))) {
      KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance("SunX509", "SunJSSE");
      keyManagerFactory.init(keyStore, password.toCharArray());
      keyManagers = keyManagerFactory.getKeyManagers();
    }

    TrustManagerFactory trustManagerFactory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    trustManagerFactory.init(trustStore);

    SSLContext context = SSLContext.getInstance("TLS", "SunJSSE");
    context.init(
        keyManagers, trustManagerFactory.getTrustManagers(), new java.security.SecureRandom());
    return context;
  }

}
