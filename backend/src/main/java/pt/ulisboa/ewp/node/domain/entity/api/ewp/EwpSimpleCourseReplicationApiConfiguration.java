package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import eu.erasmuswithoutpaper.api.courses.replication.v1.SimpleCourseReplicationV1;
import java.util.Collection;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpSimpleCourseReplicationApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Simple Course Replication API";

  private String url;
  private boolean modifiedSinceSupported;

  public EwpSimpleCourseReplicationApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      boolean modifiedSinceSupported) {
    super(supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.url = url;
    this.modifiedSinceSupported = modifiedSinceSupported;
  }

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isModifiedSinceSupported() {
    return modifiedSinceSupported;
  }

  public void setModifiedSinceSupported(boolean modifiedSinceSupported) {
    this.modifiedSinceSupported = modifiedSinceSupported;
  }

  public static EwpSimpleCourseReplicationApiConfiguration create(
      SimpleCourseReplicationV1 apiElement) {
    return new EwpSimpleCourseReplicationApiConfiguration(
        apiElement.getUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.isSupportsModifiedSince());
  }

  @Override
  public String toString() {
    return "EwpSimpleCourseReplicationApiConfiguration{"
        + "url='"
        + url
        + '\''
        + ", modifiedSinceSupported="
        + modifiedSinceSupported
        + '}';
  }
}
