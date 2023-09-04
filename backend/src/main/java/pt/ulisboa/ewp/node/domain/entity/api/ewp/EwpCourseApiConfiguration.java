package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.courses.v0.CoursesV0;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApi;

public class EwpCourseApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Courses API";

  private String url;
  private BigInteger maxLosIds;
  private BigInteger maxLosCodes;

  public EwpCourseApiConfiguration(
      String heiId,
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxLosIds,
      BigInteger maxLosCodes) {
    super(
        heiId,
        EwpApi.COURSES.getLocalName(),
        supportedClientAuthenticationMethods,
        supportedServerAuthenticationMethods);
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

  public static EwpCourseApiConfiguration create(String heiId, CoursesV0 apiElement) {
    return new EwpCourseApiConfiguration(
        heiId,
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxLosIds(),
        apiElement.getMaxLosCodes());
  }
}
