package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.monitoring.v1.MonitoringV1;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApi;

public class EwpMonitoringApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Monitoring API";

  private final String url;

  public EwpMonitoringApiConfiguration(
      String heiId,
      String version,
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods) {
    super(
        heiId,
        EwpApi.MONITORING.getLocalName(),
        version,
        supportedClientAuthenticationMethods,
        supportedServerAuthenticationMethods);
    this.url = url;
  }

  public static EwpMonitoringApiConfiguration create(String heiId, MonitoringV1 apiElement) {
    return new EwpMonitoringApiConfiguration(
        heiId,
        apiElement.getVersion(),
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()));
  }

  public String getUrl() {
    return url;
  }

  @Override
  public String toString() {
    return "EwpMonitoringApiConfiguration{" + "url='" + url + '\'' + '}';
  }
}
