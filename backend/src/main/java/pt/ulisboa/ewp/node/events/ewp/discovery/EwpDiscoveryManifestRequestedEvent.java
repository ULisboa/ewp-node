package pt.ulisboa.ewp.node.events.ewp.discovery;

import org.springframework.context.ApplicationEvent;

public class EwpDiscoveryManifestRequestedEvent extends ApplicationEvent {

  private final String requestedHeiId;

  public EwpDiscoveryManifestRequestedEvent(Object source, String requestedHeiId) {
    super(source);
    this.requestedHeiId = requestedHeiId;
  }

  public String getRequestedHeiId() {
    return requestedHeiId;
  }
}
