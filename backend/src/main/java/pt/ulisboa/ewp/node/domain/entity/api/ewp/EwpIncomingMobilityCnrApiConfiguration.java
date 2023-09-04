package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.imobilities.cnr.v1.ImobilityCnrV1;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApi;

public class EwpIncomingMobilityCnrApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Incoming Mobility CNR API";

  private final String url;
  private final BigInteger maxOmobilityIds;

  public EwpIncomingMobilityCnrApiConfiguration(
      String heiId,
      String version,
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxOmobilityIds) {
    super(
        heiId,
        EwpApi.INCOMING_MOBILITY_CNR.getLocalName(),
        version, supportedClientAuthenticationMethods,
        supportedServerAuthenticationMethods);
    this.url = url;
    this.maxOmobilityIds = maxOmobilityIds;
  }

  public String getUrl() {
    return url;
  }

  public BigInteger getMaxOmobilityIds() {
    return maxOmobilityIds;
  }

  public static EwpIncomingMobilityCnrApiConfiguration create(
      String heiId, ImobilityCnrV1 apiElement) {
    return new EwpIncomingMobilityCnrApiConfiguration(
        heiId,
        apiElement.getVersion(), 
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxOmobilityIds());
  }

  @Override
  public String toString() {
    return "EwpIncomingMobilityCnrApiConfiguration{" +
        "url='" + url + '\'' +
        ", maxOmobilityIds=" + maxOmobilityIds +
        '}';
  }
}
