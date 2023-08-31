package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.imobilities.v1.ImobilitiesV1;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpIncomingMobilitiesApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Outgoing Mobilities API";

  private String getUrl;
  private BigInteger maxOmobilityIds;
  private boolean sendsNotifications;

  public EwpIncomingMobilitiesApiConfiguration(
      String heiId,
      String getUrl,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxOmobilityIds,
      boolean sendsNotifications) {
    super(heiId, supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.getUrl = getUrl;
    this.maxOmobilityIds = maxOmobilityIds;
    this.sendsNotifications = sendsNotifications;
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

  public static EwpIncomingMobilitiesApiConfiguration create(String heiId, ImobilitiesV1 apiElement) {
    return new EwpIncomingMobilitiesApiConfiguration(
        heiId,
        apiElement.getGetUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxOmobilityIds(),
        apiElement.getSendsNotifications() != null);
  }

  @Override
  public String toString() {
    return "EwpIncomingMobilitiesApiConfiguration{"
        + "getUrl='"
        + getUrl
        + '\''
        + ", maxIiaIds="
        + maxOmobilityIds
        + ", sendsNotifications="
        + sendsNotifications
        + '}';
  }
}
