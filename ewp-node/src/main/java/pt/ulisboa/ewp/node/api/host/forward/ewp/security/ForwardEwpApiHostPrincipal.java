package pt.ulisboa.ewp.node.api.host.forward.ewp.security;

import pt.ulisboa.ewp.node.domain.entity.Host;

public class ForwardEwpApiHostPrincipal {

  private Host host;

  public ForwardEwpApiHostPrincipal(Host host) {
    this.host = host;
  }

  public Host getHost() {
    return host;
  }

  public void setHost(Host host) {
    this.host = host;
  }

  @Override
  public String toString() {
    return host.getCode();
  }
}
