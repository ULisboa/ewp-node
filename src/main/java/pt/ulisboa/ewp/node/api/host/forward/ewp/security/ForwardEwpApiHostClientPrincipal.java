package pt.ulisboa.ewp.node.api.host.forward.ewp.security;

import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;

public class ForwardEwpApiHostClientPrincipal {

  private HostForwardEwpApiClient hostForwardEwpApiClient;

  public ForwardEwpApiHostClientPrincipal(HostForwardEwpApiClient hostForwardEwpApiClient) {
    this.hostForwardEwpApiClient = hostForwardEwpApiClient;
  }

  public HostForwardEwpApiClient getHostForwardEwpApiClient() {
    return hostForwardEwpApiClient;
  }

  public void setHostForwardEwpApiClient(
      HostForwardEwpApiClient hostForwardEwpApiClient) {
    this.hostForwardEwpApiClient = hostForwardEwpApiClient;
  }

  @Override
  public String toString() {
    return this.hostForwardEwpApiClient.getId();
  }
}
