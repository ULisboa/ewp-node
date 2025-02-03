package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import java.util.Collection;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpApiConfiguration {

  private final String heiId;
  private final String apiName;
  private final String version;
  private Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods;
  private Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods;

  public EwpApiConfiguration(
      String heiId, String apiName, String version, Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods) {
    this.heiId = heiId;
    this.apiName = apiName;
    this.version = version;
    this.supportedClientAuthenticationMethods = supportedClientAuthenticationMethods;
    this.supportedServerAuthenticationMethods = supportedServerAuthenticationMethods;
  }

  public String getHeiId() {
    return heiId;
  }

  public String getApiName() {
    return apiName;
  }

  public String getVersion() {
    return version;
  }

  /**
   * Returns the "best" supported client API authentication method using a predefined list of
   * authentication methods order.
   */
  public EwpAuthenticationMethod getBestClientSupportedAuthenticationMethod() {
    for (EwpAuthenticationMethod authenticationMethod : EwpAuthenticationMethod.AUTHENTICATION_METHODS_BY_PREFERENTIAL_ORDER) {
      if (supportsClientAuthenticationMethod(authenticationMethod)) {
        return authenticationMethod;
      }
    }

    throw new IllegalStateException(
        "Failed to find an admissible authentication method for API: " + this);
  }

  public Collection<EwpClientAuthenticationConfiguration> getSupportedClientAuthenticationMethods() {
    return supportedClientAuthenticationMethods;
  }

  public void setSupportedClientAuthenticationMethods(
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods) {
    this.supportedClientAuthenticationMethods = supportedClientAuthenticationMethods;
  }

  public Collection<EwpServerAuthenticationConfiguration> getSupportedServerAuthenticationMethods() {
    return supportedServerAuthenticationMethods;
  }

  public void setSupportedServerAuthenticationMethods(
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods) {
    this.supportedServerAuthenticationMethods = supportedServerAuthenticationMethods;
  }

  public boolean supportsServerAuthenticationMethod(EwpAuthenticationMethod authenticationMethod) {
    return supportedServerAuthenticationMethods.stream()
        .anyMatch(c -> authenticationMethod == c.getAuthenticationMethod());
  }

  public boolean supportsClientAuthenticationMethod(EwpAuthenticationMethod authenticationMethod) {
    return supportedClientAuthenticationMethods.stream()
        .anyMatch(c -> authenticationMethod == c.getAuthenticationMethod());
  }
}
