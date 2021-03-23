package pt.ulisboa.ewp.node.client.ewp;

import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications.EwpApiGeneralSpecification;

public abstract class EwpApiClient<C extends EwpApiConfiguration> {

  protected final RegistryClient registryClient;
  protected final EwpClient ewpClient;

  public EwpApiClient(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public C getApiConfigurationForHeiId(String heiId) {
    return getApiGeneralSpecification().getConfigurationForHeiId(registryClient, heiId);
  }

  public abstract EwpApiGeneralSpecification<?, C> getApiGeneralSpecification();
}
