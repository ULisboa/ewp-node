package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server;

public abstract class EwpServerAuthenticationConfiguration {

  public abstract boolean isHttpSignature();

  public abstract boolean isTlsCertificate();
}
