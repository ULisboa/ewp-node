package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetV1;
import java.math.BigInteger;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApi;

public class EwpFactsheetApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Factsheet API";

  private String url;
  private BigInteger maxHeiIds;

  public EwpFactsheetApiConfiguration(
      String heiId,
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      BigInteger maxHeiIds) {
    super(
        heiId,
        EwpApi.FACTSHEETS.getLocalName(),
        supportedClientAuthenticationMethods,
        supportedServerAuthenticationMethods);
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

  public static EwpFactsheetApiConfiguration create(String heiId, FactsheetV1 apiElement) {
    return new EwpFactsheetApiConfiguration(
        heiId,
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxHeiIds());
  }

  @Override
  public String toString() {
    return "EwpFactsheetApiConfiguration{" +
        "url='" + url + '\'' +
        ", maxHeiIds=" + maxHeiIds +
        '}';
  }
}
