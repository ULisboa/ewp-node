package pt.ulisboa.ewp.node.domain.dto.communication.log.host.plugin;

import pt.ulisboa.ewp.node.domain.dto.communication.log.function.call.FunctionCallCommunicationLogDetailDto;

public class HostPluginFunctionCallCommunicationLogDetailDto
    extends FunctionCallCommunicationLogDetailDto {

  private String hostPluginId;

  public String getHostPluginId() {
    return hostPluginId;
  }

  public void setHostPluginId(String hostPluginId) {
    this.hostPluginId = hostPluginId;
  }
}
