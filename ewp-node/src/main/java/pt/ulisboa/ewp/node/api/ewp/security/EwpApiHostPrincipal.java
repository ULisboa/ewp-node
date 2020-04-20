package pt.ulisboa.ewp.node.api.ewp.security;

import java.io.Serializable;
import java.util.Collection;

public class EwpApiHostPrincipal implements Serializable {

  private final Collection<String> heiIdsCoveredByClient;

  public EwpApiHostPrincipal(Collection<String> heiIdsCoveredByClient) {
    this.heiIdsCoveredByClient = heiIdsCoveredByClient;
  }

  public Collection<String> getHeiIdsCoveredByClient() {
    return heiIdsCoveredByClient;
  }

  @Override
  public String toString() {
    return String.join("|", heiIdsCoveredByClient);
  }
}
