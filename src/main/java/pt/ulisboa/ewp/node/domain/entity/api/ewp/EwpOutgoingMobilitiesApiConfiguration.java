package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpOutgoingMobilitiesApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Outgoing Mobilities API";

  private String indexUrl;
  private String getUrl;
  private BigInteger maxOmobilityIds;
  private boolean sendsNotifications;

  public EwpOutgoingMobilitiesApiConfiguration(
      String indexUrl,
      String getUrl,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxOmobilityIds,
      boolean sendsNotifications) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.indexUrl = indexUrl;
    this.getUrl = getUrl;
    this.maxOmobilityIds = maxOmobilityIds;
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

  public BigInteger getMaxOmobilityIds() {
    return maxOmobilityIds;
  }

  public void setMaxOmobilityIds(BigInteger maxOmobilityIds) {
    this.maxOmobilityIds = maxOmobilityIds;
  }

  public boolean isSendsNotifications() {
    return sendsNotifications;
  }

  public void setSendsNotifications(boolean sendsNotifications) {
    this.sendsNotifications = sendsNotifications;
  }

  @Override
  public String toString() {
    return "EwpOutgoingMobilitiesApiConfiguration{"
        + "indexUrl='"
        + indexUrl
        + '\''
        + ", getUrl='"
        + getUrl
        + '\''
        + ", maxIiaIds="
        + maxOmobilityIds
        + ", sendsNotifications="
        + sendsNotifications
        + '}';
  }
}