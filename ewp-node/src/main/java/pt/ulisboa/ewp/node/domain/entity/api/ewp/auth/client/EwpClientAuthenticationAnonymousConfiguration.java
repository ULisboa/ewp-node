package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

public class EwpClientAuthenticationAnonymousConfiguration
    extends EwpClientAuthenticationConfiguration {

  @Override
  public boolean isAnonymous() {
    return true;
  }

  @Override
  public boolean isHttpSignature() {
    return false;
  }

  @Override
  public boolean isTlsCertificate() {
    return false;
  }
}
