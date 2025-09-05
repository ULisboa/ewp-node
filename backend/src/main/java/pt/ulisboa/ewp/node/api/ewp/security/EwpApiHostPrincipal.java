package pt.ulisboa.ewp.node.api.ewp.security;

import java.io.Serializable;
import java.util.Collection;
import java.util.Optional;

public class EwpApiHostPrincipal implements Serializable {

  private final Collection<String> heiIdsCoveredByClient;

  public EwpApiHostPrincipal(Collection<String> heiIdsCoveredByClient) {
    this.heiIdsCoveredByClient = heiIdsCoveredByClient;
  }

  public Optional<String> getHeiIdCoveredByClient() {
    if (this.heiIdsCoveredByClient.isEmpty()) {
      return Optional.empty();
    } else if (this.heiIdsCoveredByClient.size() > 1) {
      throw new IllegalStateException(
          "Only at most one HEI ID was expected but got " + this.heiIdsCoveredByClient.size());
    }
    return Optional.of(this.heiIdsCoveredByClient.iterator().next());
  }

  public Collection<String> getHeiIdsCoveredByClient() {
    return heiIdsCoveredByClient;
  }

  @Override
  public String toString() {
    return String.join("|", heiIdsCoveredByClient);
  }
}
