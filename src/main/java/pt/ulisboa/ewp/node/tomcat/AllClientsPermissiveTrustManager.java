package pt.ulisboa.ewp.node.tomcat;

import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import javax.net.ssl.TrustManager;
import javax.net.ssl.TrustManagerFactory;
import javax.net.ssl.X509TrustManager;

/** A trust manager that accepts all client certificates, as long as it is valid. */
public class AllClientsPermissiveTrustManager implements X509TrustManager {

  X509TrustManager standardTrustManager;

  public AllClientsPermissiveTrustManager() throws NoSuchAlgorithmException, KeyStoreException {
    super();
    TrustManagerFactory factory =
        TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
    factory.init((KeyStore) null);
    TrustManager[] trustManagers = factory.getTrustManagers();
    if (trustManagers.length == 0) {
      throw new NoSuchAlgorithmException("no trust manager found");
    }
    this.standardTrustManager = (X509TrustManager) trustManagers[0];
  }

  @Override
  public void checkClientTrusted(X509Certificate[] certificates, String authType)
      throws CertificateException {
    if (certificates != null && certificates.length == 1) {
      X509Certificate certificate = certificates[0];
      certificate.checkValidity();
    } else {
      standardTrustManager.checkClientTrusted(certificates, authType);
    }
  }

  @Override
  public void checkServerTrusted(X509Certificate[] certificates, String authType)
      throws CertificateException {
    standardTrustManager.checkServerTrusted(certificates, authType);
  }

  @Override
  public X509Certificate[] getAcceptedIssuers() {
    return standardTrustManager.getAcceptedIssuers();
  }
}
