package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpClientAuthenticationTlsCertificateConfiguration
    implements EwpClientAuthenticationConfiguration {

  private boolean allowSelfSigned;

  public EwpClientAuthenticationTlsCertificateConfiguration(boolean allowSelfSigned) {
    this.allowSelfSigned = allowSelfSigned;
  }

  public boolean isAllowSelfSigned() {
    return allowSelfSigned;
  }

  public void setAllowSelfSigned(boolean allowSelfSigned) {
    this.allowSelfSigned = allowSelfSigned;
  }

  @Override
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return EwpAuthenticationMethod.TLS;
  }
}
