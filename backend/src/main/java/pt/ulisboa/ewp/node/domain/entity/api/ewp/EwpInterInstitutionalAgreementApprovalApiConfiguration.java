package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalV1;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpInterInstitutionalAgreementApprovalApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Interinstitutional Agreements API";

  private String url;
  private BigInteger maxIiaIds;

  public EwpInterInstitutionalAgreementApprovalApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxIiaIds) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.maxIiaIds = maxIiaIds;
  }

  public String getUrl() {
    return url;
  }

  public BigInteger getMaxIiaIds() {
    return maxIiaIds;
  }

  public static EwpInterInstitutionalAgreementApprovalApiConfiguration create(
      IiasApprovalV1 apiElement) {
    return new EwpInterInstitutionalAgreementApprovalApiConfiguration(
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxIiaIds());
  }

  @Override
  public String toString() {
    return "EwpInterinstitutionalAgreementApprovalApiConfiguration{" +
        "url='" + url + '\'' +
        ", maxIiaIds=" + maxIiaIds +
        '}';
  }
}
