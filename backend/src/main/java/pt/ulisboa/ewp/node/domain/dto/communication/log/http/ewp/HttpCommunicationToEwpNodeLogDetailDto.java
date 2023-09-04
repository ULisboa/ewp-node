package pt.ulisboa.ewp.node.domain.dto.communication.log.http.ewp;

public class HttpCommunicationToEwpNodeLogDetailDto extends EwpHttpCommunicationLogDetailDto {

  private String targetHeiId;
  private String apiName;

  public HttpCommunicationToEwpNodeLogDetailDto(String targetHeiId, String apiName) {
    this.targetHeiId = targetHeiId;
    this.apiName = apiName;
  }

  public String getTargetHeiId() {
    return targetHeiId;
  }

  public void setTargetHeiId(String targetHeiId) {
    this.targetHeiId = targetHeiId;
  }

  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }
}
