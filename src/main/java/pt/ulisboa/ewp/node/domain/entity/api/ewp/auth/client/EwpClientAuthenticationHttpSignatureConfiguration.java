package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

public class EwpClientAuthenticationHttpSignatureConfiguration
    extends EwpClientAuthenticationConfiguration {

  @Override
  public boolean isAnonymous() {
    return false;
  }

  @Override
  public boolean isHttpSignature() {
    return true;
  }

  @Override
  public boolean isTlsCertificate() {
    return false;
  }
}
