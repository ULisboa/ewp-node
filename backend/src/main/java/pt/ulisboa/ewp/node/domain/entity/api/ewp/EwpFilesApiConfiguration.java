package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.files.v1.FileV1;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApi;

public class EwpFilesApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Files API";

  private String url;

  public EwpFilesApiConfiguration(
      String heiId,
      String version,
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods) {
    super(
        heiId,
        EwpApi.FILES.getLocalName(),
        version,
        supportedClientAuthenticationMethods,
        supportedServerAuthenticationMethods);
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public static EwpFilesApiConfiguration create(String heiId, FileV1 apiElement) {
    return new EwpFilesApiConfiguration(
        heiId,
        apiElement.getVersion(), 
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()));
  }

  @Override
  public String toString() {
    return "EwpFilesApiConfiguration{" +
        "url='" + url + '\'' +
        '}';
  }
}
