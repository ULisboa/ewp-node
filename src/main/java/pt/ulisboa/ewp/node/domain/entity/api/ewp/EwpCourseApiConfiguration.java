package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpCourseApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Courses API";

  private String url;
  private BigInteger maxLosIds;
  private BigInteger maxLosCodes;

  public EwpCourseApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxLosIds,
      BigInteger maxLosCodes) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.url = url;
    this.maxLosIds = maxLosIds;
    this.maxLosCodes = maxLosCodes;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
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

  @Override
  public String toString() {
    return "EwpCourseApiConfiguration{"
        + "url='"
        + url
        + '\''
        + ", maxLosIds="
        + maxLosIds
        + ", maxLosCodes="
        + maxLosCodes
        + '}';
  }
}
