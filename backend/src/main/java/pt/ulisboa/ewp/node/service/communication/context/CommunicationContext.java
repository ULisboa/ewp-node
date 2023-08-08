package pt.ulisboa.ewp.node.service.communication.context;

import java.io.Serializable;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

public class CommunicationContext implements Serializable {

  private final CommunicationContext parentContext;
  private CommunicationLog currentCommunicationLog;

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

  public CommunicationLog getCurrentCommunicationLog() {
    return currentCommunicationLog;
  }

  public void setCurrentCommunicationLog(CommunicationLog currentCommunicationLog) {
    this.currentCommunicationLog = currentCommunicationLog;
  }
}
