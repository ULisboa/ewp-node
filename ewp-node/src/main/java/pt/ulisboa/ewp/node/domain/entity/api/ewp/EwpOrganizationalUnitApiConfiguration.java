package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import java.math.BigInteger;
import java.util.Collection;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpOrganizationalUnitApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Organizational Units API";

  protected BigInteger maxOunitIds;
  protected BigInteger maxOunitCodes;

  public EwpOrganizationalUnitApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxOunitIds,
      BigInteger maxOunitCodes) {
    super(url, supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.maxOunitIds = maxOunitIds;
    this.maxOunitCodes = maxOunitCodes;
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
}
