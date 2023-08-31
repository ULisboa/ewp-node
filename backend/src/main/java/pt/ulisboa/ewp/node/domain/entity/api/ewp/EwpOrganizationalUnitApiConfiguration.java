package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.ounits.v2.OrganizationalUnitsV2;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpOrganizationalUnitApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Organizational Units API";

  private String url;
  private BigInteger maxOunitIds;
  private BigInteger maxOunitCodes;

  public EwpOrganizationalUnitApiConfiguration(
      String heiId,
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxOunitIds,
      BigInteger maxOunitCodes) {
    super(heiId, supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.url = url;
    this.maxOunitIds = maxOunitIds;
    this.maxOunitCodes = maxOunitCodes;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public BigInteger getMaxOunitIds() {
    return maxOunitIds;
  }

  public void setMaxOunitIds(BigInteger maxOunitIds) {
    this.maxOunitIds = maxOunitIds;
  }

  public BigInteger getMaxOunitCodes() {
    return maxOunitCodes;
  }

  public void setMaxOunitCodes(BigInteger maxOunitCodes) {
    this.maxOunitCodes = maxOunitCodes;
  }

  public static EwpOrganizationalUnitApiConfiguration create(String heiId, OrganizationalUnitsV2 apiElement) {
    return new EwpOrganizationalUnitApiConfiguration(
        heiId,
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxOunitIds(),
        apiElement.getMaxOunitCodes());
  }

  @Override
  public String toString() {
    return "EwpOrganizationalUnitApiConfiguration{"
        + "url='"
        + url
        + '\''
        + ", maxOunitIds="
        + maxOunitIds
        + ", maxOunitCodes="
        + maxOunitCodes
        + '}';
  }
}
