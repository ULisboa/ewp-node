package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpInstitutionApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Institutions API";

  private String url;
  private BigInteger maxHeiIds;

  public EwpInstitutionApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxHeiIds) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.url = url;
    this.maxHeiIds = maxHeiIds;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public BigInteger getMaxHeiIds() {
    return maxHeiIds;
  }

  public void setMaxHeiIds(BigInteger maxHeiIds) {
    this.maxHeiIds = maxHeiIds;
  }

  @Override
  public String toString() {
    return "EwpInstitutionApiConfiguration{"
        + "url='"
        + url
        + '\''
        + ", maxHeiIds="
        + maxHeiIds
        + '}';
  }
}
