package pt.ulisboa.ewp.node.domain.entity.communication.log.http.host;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Transient;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.apache.commons.lang3.StringUtils;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;

@Entity
@DiscriminatorValue(HttpCommunicationFromHostLog.TYPE)
public class HttpCommunicationFromHostLog extends HostHttpCommunicationLog {

  public static final String TYPE = "HOST_IN";

  private HostForwardEwpApiClient hostForwardEwpApiClient;
  private String apiName;
  private Integer apiMajorVersion;
  private String endpointName;
  private String targetHeiId;

  public HttpCommunicationFromHostLog() {
  }

  public HttpCommunicationFromHostLog(
      Host host,
      HostForwardEwpApiClient hostForwardEwpApiClient,
      String apiName,
      Integer apiMajorVersion,
      String endpointName,
      String targetHeiId,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication)
      throws IOException {
    super(
        host,
        request,
        response,
        startProcessingDateTime,
        endProcessingDateTime,
        observations,
        parentCommunication);
    this.hostForwardEwpApiClient = hostForwardEwpApiClient;
    this.targetHeiId = targetHeiId;
    this.apiName = apiName;
    this.apiMajorVersion = apiMajorVersion;
    this.endpointName = endpointName;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "host_forward_ewp_api_client")
  public HostForwardEwpApiClient getHostForwardEwpApiClient() {
    return hostForwardEwpApiClient;
  }

  public void setHostForwardEwpApiClient(
      HostForwardEwpApiClient hostForwardEwpApiClient) {
    this.hostForwardEwpApiClient = hostForwardEwpApiClient;
  }

  @Column(name = "api_name")
  public String getApiName() {
    return apiName;
  }

  public void setApiName(String apiName) {
    this.apiName = apiName;
  }

  @Column(name = "api_major_version")
  public Integer getApiMajorVersion() {
    return apiMajorVersion;
  }

  public void setApiMajorVersion(Integer apiMajorVersion) {
    this.apiMajorVersion = apiMajorVersion;
  }

  @Column(name = "endpoint_name")
  public String getEndpointName() {
    return endpointName;
  }

  public void setEndpointName(String endpointName) {
    this.endpointName = endpointName;
  }

  @Column(name = "target_hei_id")
  public String getTargetHeiId() {
    return targetHeiId;
  }

  public void setTargetHeiId(String targetHeiId) {
    this.targetHeiId = targetHeiId;
  }

  @Override
  @Transient
  public String getSource() {
    if (getHostForwardEwpApiClient() == null) {
      return "Unknown";
    }
    return getHostForwardEwpApiClient().getId();
  }

  @Override
  @Transient
  public String getTarget() {
    if (!StringUtils.isEmpty(getApiName())) {
      StringBuilder stringBuilder = new StringBuilder();
      if (!StringUtils.isEmpty(getTargetHeiId())) {
        stringBuilder.append(getTargetHeiId());
      }
      if (stringBuilder.length() > 0) {
        stringBuilder.append(":");
      }
      stringBuilder.append(getApiName());
      if (getApiMajorVersion() != null) {
        stringBuilder.append("[").append(getApiMajorVersion()).append("]");
      }
      if (!StringUtils.isEmpty(getEndpointName())) {
        stringBuilder.append(":").append(getEndpointName());
      }
      return stringBuilder.toString();
    }
    return super.getTarget();
  }
}
