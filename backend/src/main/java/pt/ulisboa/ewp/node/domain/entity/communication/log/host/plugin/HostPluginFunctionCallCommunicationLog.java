package pt.ulisboa.ewp.node.domain.entity.communication.log.host.plugin;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.time.ZonedDateTime;
import java.util.List;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.function.call.FunctionCallCommunicationLog;

@Entity
@DiscriminatorValue(HostPluginFunctionCallCommunicationLog.TYPE)
public class HostPluginFunctionCallCommunicationLog extends FunctionCallCommunicationLog {

  public static final String TYPE = "HOST_PLUGIN_FUNCTION_CALL";

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

  @Override
  @Transient
  public String getTarget() {
    return getHostPluginId() + ": " + super.getTarget();
  }
}
