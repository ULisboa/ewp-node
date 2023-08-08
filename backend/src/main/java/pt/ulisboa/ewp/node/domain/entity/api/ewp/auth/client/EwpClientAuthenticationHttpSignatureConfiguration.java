package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpClientAuthenticationHttpSignatureConfiguration
    implements EwpClientAuthenticationConfiguration {

  @Override
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return EwpAuthenticationMethod.HTTP_SIGNATURE;
  }
}
