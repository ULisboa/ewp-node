package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrV1;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApi;

public class EwpOutgoingMobilityLearningAgreementCnrApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Outgoing Mobility Learning Agreements CNR API";

  private final String url;
  private final BigInteger maxOmobilityIds;

  public EwpOutgoingMobilityLearningAgreementCnrApiConfiguration(
      String heiId,
      String version,
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxOmobilityIds) {
    super(
        heiId,
        EwpApi.OUTGOING_MOBILITY_LEARNING_AGREEMENT_CNR.getLocalName(),
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

  public static EwpOutgoingMobilityLearningAgreementCnrApiConfiguration create(
      String heiId, OmobilityLaCnrV1 apiElement) {
    return new EwpOutgoingMobilityLearningAgreementCnrApiConfiguration(
        heiId,
        apiElement.getVersion(), 
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxOmobilityIds());
  }

  @Override
  public String toString() {
    return "EwpOutgoingMobilityLearningAgreementCnrApiConfiguration{" +
        "url='" + url + '\'' +
        ", maxOmobilityIds=" + maxOmobilityIds +
        '}';
  }
}
