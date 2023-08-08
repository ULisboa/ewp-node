package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpServerAuthenticationHttpSignatureConfiguration
    implements EwpServerAuthenticationConfiguration {

  @Override
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return EwpAuthenticationMethod.HTTP_SIGNATURE;
  }
}
