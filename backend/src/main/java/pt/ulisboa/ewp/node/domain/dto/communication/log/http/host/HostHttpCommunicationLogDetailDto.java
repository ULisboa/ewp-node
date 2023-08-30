package pt.ulisboa.ewp.node.domain.dto.communication.log.http.host;

import pt.ulisboa.ewp.node.domain.dto.communication.log.http.HttpCommunicationLogDetailDto;

public class HostHttpCommunicationLogDetailDto extends HttpCommunicationLogDetailDto {

  private String hostCode;

  public String getHostCode() {
    return hostCode;
  }

  public void setHostCode(String hostCode) {
    this.hostCode = hostCode;
  }
}
