package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.omobilities.cnr.v1.OmobilityCnrV1;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpOutgoingMobilityCnrApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Outgoing Mobility CNR API";

  private final String url;
  private final BigInteger maxOmobilityIds;

  public EwpOutgoingMobilityCnrApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxOmobilityIds) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.url = url;
    this.maxOmobilityIds = maxOmobilityIds;
  }

  public String getUrl() {
    return url;
  }

  public BigInteger getMaxOmobilityIds() {
    return maxOmobilityIds;
  }

  public static EwpOutgoingMobilityCnrApiConfiguration create(
      OmobilityCnrV1 apiElement) {
    return new EwpOutgoingMobilityCnrApiConfiguration(
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxOmobilityIds());
  }

  @Override
  public String toString() {
    return "EwpOutgoingMobilityCnrApiConfiguration{" +
        "url='" + url + '\'' +
        ", maxOmobilityIds=" + maxOmobilityIds +
        '}';
  }
}
