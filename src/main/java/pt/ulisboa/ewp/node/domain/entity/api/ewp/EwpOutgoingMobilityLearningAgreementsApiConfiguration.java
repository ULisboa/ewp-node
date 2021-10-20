package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.omobilities.las.v1.OmobilityLasV1;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpOutgoingMobilityLearningAgreementsApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Outgoing Mobility Learning Agreements API";

  private final String indexUrl;
  private final String getUrl;
  private final String updateUrl;
  private final BigInteger maxOmobilityIds;

  public EwpOutgoingMobilityLearningAgreementsApiConfiguration(
      String indexUrl,
      String getUrl,
      String updateUrl,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxOmobilityIds) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.indexUrl = indexUrl;
    this.getUrl = getUrl;
    this.updateUrl = updateUrl;
    this.maxOmobilityIds = maxOmobilityIds;
  }

  public String getIndexUrl() {
    return indexUrl;
  }

  public String getGetUrl() {
    return getUrl;
  }

  public String getUpdateUrl() {
    return updateUrl;
  }

  public BigInteger getMaxOmobilityIds() {
    return maxOmobilityIds;
  }

  public static EwpOutgoingMobilityLearningAgreementsApiConfiguration create(
      OmobilityLasV1 apiElement) {
    return new EwpOutgoingMobilityLearningAgreementsApiConfiguration(
        apiElement.getIndexUrl(),
        apiElement.getGetUrl(),
        apiElement.getUpdateUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxOmobilityIds());
  }

  @Override
  public String toString() {
    return "EwpOutgoingMobilityLearningAgreementsApiConfiguration{" +
        "indexUrl='" + indexUrl + '\'' +
        ", getUrl='" + getUrl + '\'' +
        ", updateUrl='" + updateUrl + '\'' +
        ", maxOmobilityIds=" + maxOmobilityIds +
        '}';
  }
}
