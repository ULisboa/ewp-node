package pt.ulisboa.ewp.node.client.ewp;

import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;

public abstract class EwpApiClient<C extends EwpApiConfiguration> {

  protected final RegistryClient registryClient;
  protected final EwpClient ewpClient;

  public EwpApiClient(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public C getApiConfigurationForHeiId(String heiId) {
    return getApiVersionSpecification().getConfigurationForHeiId(registryClient, heiId);
  }

  public abstract EwpApiVersionSpecification<?, C> getApiVersionSpecification();
}
