package pt.ulisboa.ewp.node.domain.dto.communication.log.http.ewp;

import java.util.Collection;

public class HttpCommunicationFromEwpNodeLogDetailDto extends EwpHttpCommunicationLogDetailDto {

  private Collection<String> heiIdsCoveredByClient;

  public Collection<String> getHeiIdsCoveredByClient() {
    return heiIdsCoveredByClient;
  }

  public void setHeiIdsCoveredByClient(Collection<String> heiIdsCoveredByClient) {
    this.heiIdsCoveredByClient = heiIdsCoveredByClient;
  }
}
