package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

public abstract class EwpClientAuthenticationConfiguration {

  public abstract boolean isAnonymous();

  public abstract boolean isHttpSignature();

  public abstract boolean isTlsCertificate();
}
