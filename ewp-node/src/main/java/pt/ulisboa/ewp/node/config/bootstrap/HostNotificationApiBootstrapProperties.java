package pt.ulisboa.ewp.node.config.bootstrap;

public class HostNotificationApiBootstrapProperties {

  private String baseUrl;
  private String secret;

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }
}
