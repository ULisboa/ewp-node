package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpInterinstitutionalAgreementApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Interinstitutional Agreements API";

  private String indexUrl;
  private String getUrl;
  private BigInteger maxIiaIds;
  private BigInteger maxIiaCodes;
  private boolean sendsNotifications;

  public EwpInterinstitutionalAgreementApiConfiguration(
      String indexUrl,
      String getUrl,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxIiaIds,
      BigInteger maxIiaCodes,
      boolean sendsNotifications) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.indexUrl = indexUrl;
    this.getUrl = getUrl;
    this.maxIiaIds = maxIiaIds;
    this.maxIiaCodes = maxIiaCodes;
    this.sendsNotifications = sendsNotifications;
  }

  public String getIndexUrl() {
    return indexUrl;
  }

  public void setIndexUrl(String indexUrl) {
    this.indexUrl = indexUrl;
  }

  public String getGetUrl() {
    return getUrl;
  }

  public void setGetUrl(String getUrl) {
    this.getUrl = getUrl;
  }

  public BigInteger getMaxIiaIds() {
    return maxIiaIds;
  }

  public void setMaxIiaIds(BigInteger maxIiaIds) {
    this.maxIiaIds = maxIiaIds;
  }

  public BigInteger getMaxIiaCodes() {
    return maxIiaCodes;
  }

  public void setMaxIiaCodes(BigInteger maxIiaCodes) {
    this.maxIiaCodes = maxIiaCodes;
  }

  public boolean isSendsNotifications() {
    return sendsNotifications;
  }

  public void setSendsNotifications(boolean sendsNotifications) {
    this.sendsNotifications = sendsNotifications;
  }

  @Override
  public String toString() {
    return "EwpInterinstitutionalAgreementApiConfiguration{"
        + "indexUrl='"
        + indexUrl
        + '\''
        + ", getUrl='"
        + getUrl
        + '\''
        + ", maxIiaIds="
        + maxIiaIds
        + ", maxIiaCodes="
        + maxIiaCodes
        + ", sendsNotifications="
        + sendsNotifications
        + '}';
  }
}
