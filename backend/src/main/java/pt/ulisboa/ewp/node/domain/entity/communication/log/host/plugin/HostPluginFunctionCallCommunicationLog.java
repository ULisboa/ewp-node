package pt.ulisboa.ewp.node.domain.entity.communication.log.host.plugin;

import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.function.call.FunctionCallCommunicationLog;

@Entity
@DiscriminatorValue("HOST_PLUGIN_FUNCTION_CALL")
public class HostPluginFunctionCallCommunicationLog extends FunctionCallCommunicationLog {

  private String hostPluginId;

  public HostPluginFunctionCallCommunicationLog() {}

  public HostPluginFunctionCallCommunicationLog(
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      CommunicationLog parentCommunication,
      String hostPluginId,
      String className,
      String method,
      List<Object> arguments) {
    super(
        startProcessingDateTime,
        endProcessingDateTime,
        observations,
        parentCommunication,
        className,
        method,
        arguments);
    this.hostPluginId = hostPluginId;
  }

  @Column(name = "host_plugin_id")
  public String getHostPluginId() {
    return hostPluginId;
  }

  public void setHostPluginId(String hostPluginId) {
    this.hostPluginId = hostPluginId;
  }
}
