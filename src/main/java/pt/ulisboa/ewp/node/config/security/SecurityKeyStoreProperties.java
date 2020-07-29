package pt.ulisboa.ewp.node.config.security;

public class SecurityKeyStoreProperties {

  private boolean importFromSsl;
  private long cacheValidityInSeconds;
  private String password;

  public boolean isImportFromSsl() {
    return importFromSsl;
  }

  public void setImportFromSsl(boolean importFromSsl) {
    this.importFromSsl = importFromSsl;
  }

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
