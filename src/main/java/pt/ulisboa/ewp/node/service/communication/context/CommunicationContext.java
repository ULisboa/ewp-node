package pt.ulisboa.ewp.node.service.communication.context;

import java.io.Serializable;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

public class CommunicationContext implements Serializable {

  private CommunicationLog currentCommunicationLog;

  public CommunicationContext() {
    this(null);
  }

  public CommunicationContext(CommunicationLog currentCommunicationLog) {
    this.currentCommunicationLog = currentCommunicationLog;
  }

  public CommunicationLog getCurrentCommunicationLog() {
    return currentCommunicationLog;
  }

  public void setCurrentCommunicationLog(CommunicationLog currentCommunicationLog) {
    this.currentCommunicationLog = currentCommunicationLog;
  }
}
