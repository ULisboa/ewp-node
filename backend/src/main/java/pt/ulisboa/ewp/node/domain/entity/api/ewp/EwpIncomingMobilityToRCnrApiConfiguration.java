package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.imobilities.tors.cnr.v1.ImobilityTorCnrV1;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpIncomingMobilityToRCnrApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Incoming Mobility ToR CNR API";

  private final String url;
  private final BigInteger maxOmobilityIds;

  public EwpIncomingMobilityToRCnrApiConfiguration(
      String heiId,
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxOmobilityIds) {
    super(heiId, supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.url = url;
    this.maxOmobilityIds = maxOmobilityIds;
  }

  public String getUrl() {
    return url;
  }

  public BigInteger getMaxOmobilityIds() {
    return maxOmobilityIds;
  }

  public static EwpIncomingMobilityToRCnrApiConfiguration create(
      String heiId, ImobilityTorCnrV1 apiElement) {
    return new EwpIncomingMobilityToRCnrApiConfiguration(
        heiId,
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxOmobilityIds());
  }

  @Override
  public String toString() {
    return "EwpIncomingMobilityToRCnrApiConfiguration{" +
        "url='" + url + '\'' +
        ", maxOmobilityIds=" + maxOmobilityIds +
        '}';
  }
}
