package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import java.util.Collection;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpSimpleCourseReplicationApiConfiguration extends EwpApiConfiguration {

  public static final String API_NAME = "Simple Course Replication API";

  protected boolean modifiedSinceSupported;

  public EwpSimpleCourseReplicationApiConfiguration(
      String url,
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods,
      boolean modifiedSinceSupported) {
    super(url, supportedClientAuthenticationMethods, supportedServerAuthenticationMethods);
    this.modifiedSinceSupported = modifiedSinceSupported;
  }

  public boolean isModifiedSinceSupported() {
    return modifiedSinceSupported;
  }

  public void setModifiedSinceSupported(boolean modifiedSinceSupported) {
    this.modifiedSinceSupported = modifiedSinceSupported;
  }
}
