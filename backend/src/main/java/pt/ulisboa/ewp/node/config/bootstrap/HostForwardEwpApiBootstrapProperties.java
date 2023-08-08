package pt.ulisboa.ewp.node.config.bootstrap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class HostForwardEwpApiBootstrapProperties {

  private List<HostForwardEwpApiClientBootstrapProperties> clients = new ArrayList<>();

  public List<HostForwardEwpApiClientBootstrapProperties> getClients() {
    return clients;
  }

  public void setClients(
      List<HostForwardEwpApiClientBootstrapProperties> clients) {
    this.clients = clients;
  }

  public Optional<HostForwardEwpApiClientBootstrapProperties> getClientById(String id) {
    return this.clients.stream().filter(c -> c.getId().equals(id)).findFirst();
  }
}
