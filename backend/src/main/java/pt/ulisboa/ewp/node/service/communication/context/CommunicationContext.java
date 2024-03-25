package pt.ulisboa.ewp.node.service.communication.context;

import java.io.Serializable;
import java.util.Collection;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;

public class CommunicationContext implements Serializable {

  private final CommunicationContext parentContext;
  private CommunicationLog currentCommunicationLog;
  private Collection<EwpChangeNotification> currentEwpChangeNotifications;

  public CommunicationContext() {
    this(null, null);
  }

  public CommunicationContext(CommunicationContext parentContext, CommunicationLog currentCommunicationLog) {
    this.parentContext = parentContext;
    this.currentCommunicationLog = currentCommunicationLog;
  }

  public CommunicationContext getParentContext() {
    return parentContext;
  }

  public boolean hasCurrentCommunicationLog() {
    return getCurrentCommunicationLog() != null;
  }

  public CommunicationLog getCurrentCommunicationLog() {
    return currentCommunicationLog;
  }

  public void setCurrentCommunicationLog(CommunicationLog currentCommunicationLog) {
    this.currentCommunicationLog = currentCommunicationLog;
  }

  public Collection<EwpChangeNotification> getCurrentEwpChangeNotifications() {
    return currentEwpChangeNotifications;
  }

  public void setCurrentEwpChangeNotifications(
      Collection<EwpChangeNotification> currentEwpChangeNotifications) {
    this.currentEwpChangeNotifications = currentEwpChangeNotifications;
  }
}
