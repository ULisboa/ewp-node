package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public interface EwpClientAuthenticationConfiguration {

  EwpAuthenticationMethod getAuthenticationMethod();
}
