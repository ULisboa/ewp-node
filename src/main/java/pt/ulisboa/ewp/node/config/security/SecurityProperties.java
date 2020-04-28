package pt.ulisboa.ewp.node.config.security;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties(prefix = "security")
public class SecurityProperties {

  private boolean allowMissingClientCertificate;
  private SecurityClientTlsProperties clientTls;
  private SecurityKeyStoreProperties keyStore;
  private SecurityRestProperties api;

  public boolean isAllowMissingClientCertificate() {
    return allowMissingClientCertificate;
  }

  public void setAllowMissingClientCertificate(boolean allowMissingClientCertificate) {
    this.allowMissingClientCertificate = allowMissingClientCertificate;
  }

  public SecurityClientTlsProperties getClientTls() {
    return clientTls;
  }

  public void setClientTls(SecurityClientTlsProperties clientTls) {
    this.clientTls = clientTls;
  }

  public SecurityKeyStoreProperties getKeyStore() {
    return keyStore;
  }

  public void setKeyStore(SecurityKeyStoreProperties keyStore) {
    this.keyStore = keyStore;
  }

  public SecurityRestProperties getApi() {
    return api;
  }

  public void setApi(SecurityRestProperties api) {
    this.api = api;
  }
}
