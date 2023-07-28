package pt.ulisboa.ewp.node.domain.entity.communication.log.host;

import java.time.ZonedDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

@Entity
@DiscriminatorValue("HOST_PLUGIN_CALL")
public class HostPluginCallCommunicationLog extends CommunicationLog {

  private String hostPluginId;
  private String invocation;

  public HostPluginCallCommunicationLog() {}

  public HostPluginCallCommunicationLog(
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      CommunicationLog parentCommunication,
      String hostPluginId,
      String invocation) {
    super(startProcessingDateTime, endProcessingDateTime, observations, parentCommunication);
    this.hostPluginId = hostPluginId;
    this.invocation = invocation;
  }

  public String getHostPluginId() {
    return hostPluginId;
  }

  public void setHostPluginId(String hostPluginId) {
    this.hostPluginId = hostPluginId;
  }

  public String getInvocation() {
    return invocation;
  }

  public void setInvocation(String invocation) {
    this.invocation = invocation;
  }
}
