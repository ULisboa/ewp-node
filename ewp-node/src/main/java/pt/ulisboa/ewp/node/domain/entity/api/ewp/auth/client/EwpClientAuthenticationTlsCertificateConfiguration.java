package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

public class EwpClientAuthenticationTlsCertificateConfiguration
    extends EwpClientAuthenticationConfiguration {

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
  public boolean isAnonymous() {
    return false;
  }

  @Override
  public boolean isHttpSignature() {
    return false;
  }

  @Override
  public boolean isTlsCertificate() {
    return true;
  }
}
