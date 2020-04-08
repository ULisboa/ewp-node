package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server;

public class EwpServerAuthenticationTlsCertificateConfiguration
    extends EwpServerAuthenticationConfiguration {

  @Override
  public boolean isHttpSignature() {
    return false;
  }

  @Override
  public boolean isTlsCertificate() {
    return true;
  }
}
