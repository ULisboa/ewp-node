package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.iias.cnr.v2.IiaCnrV2;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpInterInstitutionalAgreementCnrApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "InterInstitutional Agreement CNR API";

  private final String url;

  public EwpInterInstitutionalAgreementCnrApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public static EwpInterInstitutionalAgreementCnrApiConfiguration create(
      IiaCnrV2 apiElement) {
    return new EwpInterInstitutionalAgreementCnrApiConfiguration(
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()));
  }

  @Override
  public String toString() {
    return "EwpInterInstitutionalAgreementCnrApiConfiguration{" +
        "url='" + url + '\'' +
        '}';
  }
}
