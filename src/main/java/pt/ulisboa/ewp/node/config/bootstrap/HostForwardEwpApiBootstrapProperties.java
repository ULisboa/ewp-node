package pt.ulisboa.ewp.node.config.bootstrap;

import java.util.ArrayList;
import java.util.List;

public class HostForwardEwpApiBootstrapProperties {

  private List<HostForwardEwpApiClientBootstrapProperties> clients = new ArrayList<>();

  public List<HostForwardEwpApiClientBootstrapProperties> getClients() {
    return clients;
  }

  public void setClients(
      List<HostForwardEwpApiClientBootstrapProperties> clients) {
    this.clients = clients;
  }
}
