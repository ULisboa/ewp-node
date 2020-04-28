package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import java.math.BigInteger;
import java.util.Collection;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpCourseApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Courses API";

  protected BigInteger maxLosIds;
  protected BigInteger maxLosCodes;

  public EwpCourseApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxLosIds,
      BigInteger maxLosCodes) {
    super(url, supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.maxLosIds = maxLosIds;
    this.maxLosCodes = maxLosCodes;
  }

  public BigInteger getMaxLosIds() {
    return maxLosIds;
  }

  public void setMaxLosIds(BigInteger maxLosIds) {
    this.maxLosIds = maxLosIds;
  }

  public BigInteger getMaxLosCodes() {
    return maxLosCodes;
  }

  public void setMaxLosCodes(BigInteger maxLosCodes) {
    this.maxLosCodes = maxLosCodes;
  }
}
