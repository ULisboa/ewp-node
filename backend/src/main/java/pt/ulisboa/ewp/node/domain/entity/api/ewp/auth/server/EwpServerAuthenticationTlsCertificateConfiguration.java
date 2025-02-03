package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpServerAuthenticationTlsCertificateConfiguration
    implements EwpServerAuthenticationConfiguration {

  @Override
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return EwpAuthenticationMethod.TLS_CERTIFICATE;
  }
}
