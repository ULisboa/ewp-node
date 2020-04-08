package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server;

public class EwpServerAuthenticationHttpSignatureConfiguration
    extends EwpServerAuthenticationConfiguration {

  @Override
  public boolean isHttpSignature() {
    return true;
  }

  @Override
  public boolean isTlsCertificate() {
    return false;
  }
}
