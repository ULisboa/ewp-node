package pt.ulisboa.ewp.node.domain.entity.communication.log.http.host;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
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

  public HttpCommunicationFromHostLog() {
  }

  public HttpCommunicationFromHostLog(
      Host host,
      HostForwardEwpApiClient hostForwardEwpApiClient,
      String apiName,
      Integer apiMajorVersion,
      String endpointName,
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
