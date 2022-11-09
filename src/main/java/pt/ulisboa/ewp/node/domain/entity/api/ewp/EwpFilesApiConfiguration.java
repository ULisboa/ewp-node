package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.files.v1.FileV1;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpFilesApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Files API";

  private String url;

  public EwpFilesApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.url = url;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public static EwpFilesApiConfiguration create(FileV1 apiElement) {
    return new EwpFilesApiConfiguration(
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
