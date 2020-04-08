package pt.ulisboa.ewp.node.config.security;

public class SecurityKeyStoreProperties {

  private long cacheValidityInSeconds;
  private String password;

  public long getCacheValidityInSeconds() {
    return cacheValidityInSeconds;
  }

  public void setCacheValidityInSeconds(long cacheValidityInSeconds) {
    this.cacheValidityInSeconds = cacheValidityInSeconds;
  }

  public String getPassword() {
    return password;
  }

  public void setPassword(String password) {
    this.password = password;
  }
}
