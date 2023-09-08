package pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.List;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.utils.communication.log.CommunicationLogWarningCode;

@Entity
@DiscriminatorValue(HttpCommunicationToEwpNodeLog.TYPE)
public class HttpCommunicationToEwpNodeLog extends EwpHttpCommunicationLog {

  public static final String TYPE = "EWP_OUT";

  private String targetHeiId;
  private String apiName;
  private String apiVersion;
  private String endpointName;
  private String serverDeveloperMessage;
  private boolean reportedToMonitoring;

  public HttpCommunicationToEwpNodeLog() {}

  public HttpCommunicationToEwpNodeLog(
      String targetHeiId,
      String apiName,
      String apiVersion,
      String endpointName,
      EwpAuthenticationMethod authenticationMethod,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String serverDeveloperMessage,
      String observations,
      HttpCommunicationLog parentCommunication)
      throws IOException {
    super(
        authenticationMethod,
        request,
        response,
        startProcessingDateTime,
        endProcessingDateTime,
        observations,
        parentCommunication);
    this.targetHeiId = targetHeiId;
    this.apiName = apiName;
    this.apiVersion = apiVersion;
    this.endpointName = endpointName;
    this.serverDeveloperMessage = serverDeveloperMessage;
    this.reportedToMonitoring = false;
  }
  
  @Column(name = "target_hei_id")
  public String getTargetHeiId() {
    return targetHeiId;
  }

  public void setTargetHeiId(String targetHeiId) {
    this.targetHeiId = targetHeiId;
  }

  @Column(name = "api_name")
  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  @Column(name = "api_version")
  public String getApiVersion() {
    return apiVersion;
  }

  public void setApiVersion(String apiVersion) {
    this.apiVersion = apiVersion;
  }

  @Column(name = "endpoint_name")
  public String getEndpointName() {
    return endpointName;
  }

  public void setEndpointName(String endpointName) {
    this.endpointName = endpointName;
  }

  @Column(name = "server_developer_message")
  public String getServerDeveloperMessage() {
    return serverDeveloperMessage;
  }

  public void setServerDeveloperMessage(String serverDeveloperMessage) {
    this.serverDeveloperMessage = serverDeveloperMessage;
  }

  @Column(name = "reported_to_monitoring")
  public boolean isReportedToMonitoring() {
    return reportedToMonitoring;
  }

  public void setReportedToMonitoring(boolean reportedToMonitoring) {
    this.reportedToMonitoring = reportedToMonitoring;
  }

  @Override
  @Transient
  public String getTarget() {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(getTargetHeiId()).append(":").append(getApiName()).append("[").append(getApiVersion()).append("]");
    if (!StringUtils.isEmpty(getEndpointName())) {
      stringBuilder.append(":").append(getEndpointName());
    }
    return stringBuilder.toString();
  }

  @Override
  @Transient
  public List<CommunicationLogWarningCode> getWarningCodes() {
    List<CommunicationLogWarningCode> result = super.getWarningCodes();
    if (!isReportedToMonitoring()) {
      result.add(CommunicationLogWarningCode.ERROR_NOT_REPORTED_TO_MONITORING);
    }
    return result;
  }
}
