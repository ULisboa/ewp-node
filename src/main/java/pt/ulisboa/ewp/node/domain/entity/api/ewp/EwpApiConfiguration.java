package pt.ulisboa.ewp.node.domain.entity.api.ewp;

import java.util.Collection;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client.EwpClientAuthenticationConfiguration;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server.EwpServerAuthenticationConfiguration;

public class EwpApiConfiguration {

  private Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods;
  private Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods;

  public EwpApiConfiguration(
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods,
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods) {
    this.supportedClientAuthenticationMethods = supportedClientAuthenticationMethods;
    this.supportedServerAuthenticationMethods = supportedServerAuthenticationMethods;
  }

  public Collection<EwpClientAuthenticationConfiguration>
      getSupportedClientAuthenticationMethods() {
    return supportedClientAuthenticationMethods;
  }

  public void setSupportedClientAuthenticationMethods(
      Collection<EwpClientAuthenticationConfiguration> supportedClientAuthenticationMethods) {
    this.supportedClientAuthenticationMethods = supportedClientAuthenticationMethods;
  }

  public Collection<EwpServerAuthenticationConfiguration>
  getSupportedServerAuthenticationMethods() {
    return supportedServerAuthenticationMethods;
  }

  public void setSupportedServerAuthenticationMethods(
      Collection<EwpServerAuthenticationConfiguration> supportedServerAuthenticationMethods) {
    this.supportedServerAuthenticationMethods = supportedServerAuthenticationMethods;
  }

  public boolean supportsAuthenticationMethod(EwpAuthenticationMethod authenticationMethod) {
    return supportsClientAuthenticationMethod(authenticationMethod)
        && supportsServerAuthenticationMethod(authenticationMethod);
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
