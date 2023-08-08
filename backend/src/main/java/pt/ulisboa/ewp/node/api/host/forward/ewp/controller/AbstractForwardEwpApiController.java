package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

public abstract class AbstractForwardEwpApiController {

  private final RegistryClient registryClient;

  protected AbstractForwardEwpApiController(RegistryClient registryClient) {
    this.registryClient = registryClient;
  }

  public RegistryClient getRegistryClient() {
    return registryClient;
  }
}
