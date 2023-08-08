package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpClientAuthenticationAnonymousConfiguration
    implements EwpClientAuthenticationConfiguration {

  @Override
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return EwpAuthenticationMethod.ANONYMOUS;
  }
}
