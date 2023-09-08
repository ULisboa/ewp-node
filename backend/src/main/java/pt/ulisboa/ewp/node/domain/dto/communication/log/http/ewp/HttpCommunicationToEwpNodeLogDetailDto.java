package pt.ulisboa.ewp.node.domain.dto.communication.log.http.ewp;

public class HttpCommunicationToEwpNodeLogDetailDto extends EwpHttpCommunicationLogDetailDto {

  private String targetHeiId;
  private String apiName;
  private String apiVersion;
  private String endpointName;
  private String serverDeveloperMessage;
  private boolean reportedToMonitoring;

  public HttpCommunicationToEwpNodeLogDetailDto(
      String targetHeiId, String apiName, String apiVersion, String endpointName,
      String serverDeveloperMessage, boolean reportedToMonitoring) {
    this.targetHeiId = targetHeiId;
    this.apiName = apiName;
    this.apiVersion = apiVersion;
    this.endpointName = endpointName;
    this.serverDeveloperMessage = serverDeveloperMessage;
    this.reportedToMonitoring = reportedToMonitoring;
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

  public String getEndpointName() {
    return endpointName;
  }

  public void setEndpointName(String endpointName) {
    this.endpointName = endpointName;
  }

  public String getServerDeveloperMessage() {
    return serverDeveloperMessage;
  }

  public void setServerDeveloperMessage(String serverDeveloperMessage) {
    this.serverDeveloperMessage = serverDeveloperMessage;
  }

  public boolean isReportedToMonitoring() {
    return reportedToMonitoring;
  }

  public void setReportedToMonitoring(boolean reportedToMonitoring) {
    this.reportedToMonitoring = reportedToMonitoring;
  }
}
