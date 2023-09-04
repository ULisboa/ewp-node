package pt.ulisboa.ewp.node.domain.dto.communication.log.http.ewp;

public class HttpCommunicationToEwpNodeLogDetailDto extends EwpHttpCommunicationLogDetailDto {

  private String targetHeiId;
  private String apiName;
  private String apiVersion;

  public HttpCommunicationToEwpNodeLogDetailDto(
      String targetHeiId, String apiName, String apiVersion) {
    this.targetHeiId = targetHeiId;
    this.apiName = apiName;
    this.apiVersion = apiVersion;
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

  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }
}
