package pt.ulisboa.ewp.node.service.communication.context;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

public class CommunicationContext implements Serializable {

  private CommunicationLog currentCommunicationLog;
  private final StringBuffer observationsBuffer = new StringBuffer();

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

  public void registerException(Throwable throwable) {
    StringWriter stringWriter = new StringWriter();
    PrintWriter printWriter = new PrintWriter(stringWriter);
    throwable.printStackTrace(printWriter);
    this.addObservation(stringWriter.toString());
  }

  public void addObservation(String message) {
    if (this.observationsBuffer.length() > 0) {
      this.observationsBuffer.append(System.lineSeparator());
    }
    this.observationsBuffer.append(message);
  }

  public String getObservations() {
    return this.observationsBuffer.toString();
  }
}
