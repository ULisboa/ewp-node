package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public interface EwpServerAuthenticationConfiguration {

  EwpAuthenticationMethod getAuthenticationMethod();
}
