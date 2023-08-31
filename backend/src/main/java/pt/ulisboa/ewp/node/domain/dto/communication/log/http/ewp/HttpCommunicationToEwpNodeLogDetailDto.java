package pt.ulisboa.ewp.node.domain.dto.communication.log.http.ewp;

public class HttpCommunicationToEwpNodeLogDetailDto extends EwpHttpCommunicationLogDetailDto {

  private String targetHeiId;

  public HttpCommunicationToEwpNodeLogDetailDto(String targetHeiId) {
    this.targetHeiId = targetHeiId;
  }

  public String getTargetHeiId() {
    return targetHeiId;
  }

  public void setTargetHeiId(String targetHeiId) {
    this.targetHeiId = targetHeiId;
  }
}
