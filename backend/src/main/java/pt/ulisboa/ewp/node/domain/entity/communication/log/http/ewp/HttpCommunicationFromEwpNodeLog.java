package pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collection;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.Transient;
import org.apache.commons.lang3.StringUtils;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;

@Entity
@DiscriminatorValue(HttpCommunicationFromEwpNodeLog.TYPE)
public class HttpCommunicationFromEwpNodeLog extends EwpHttpCommunicationLog {

  public static final String TYPE = "EWP_IN";

  private Collection<String> heiIdsCoveredByClient;
  private String apiName;
  private Integer apiMajorVersion;
  private String endpointName;

  public HttpCommunicationFromEwpNodeLog() {}

  public HttpCommunicationFromEwpNodeLog(
      EwpAuthenticationMethod authenticationMethod,
      Collection<String> heiIdsCoveredByClient,
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
        authenticationMethod,
        request,
        response,
        startProcessingDateTime,
        endProcessingDateTime,
        observations,
        parentCommunication);
    this.heiIdsCoveredByClient = heiIdsCoveredByClient;
    this.apiName = apiName;
    this.apiMajorVersion = apiMajorVersion;
    this.endpointName = endpointName;
  }

  @ElementCollection
  @CollectionTable(name = "COMMUNICATION_FROM_EWP_NODE_LOG_HEI_IDS_COVERED_BY_CLIENT")
  public Collection<String> getHeiIdsCoveredByClient() {
    return heiIdsCoveredByClient;
  }

  public void setHeiIdsCoveredByClient(Collection<String> heiIdsCoveredByClient) {
    this.heiIdsCoveredByClient = heiIdsCoveredByClient;
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

  public void updateAuthenticationData(EwpApiHostAuthenticationToken authenticationToken) {
    this.setAuthenticationMethod(authenticationToken.getAuthenticationMethod());
    this.heiIdsCoveredByClient = authenticationToken.getPrincipal().getHeiIdsCoveredByClient();
  }

  @Override
  @Transient
  public String getSource() {
    if (this.heiIdsCoveredByClient == null || this.heiIdsCoveredByClient.isEmpty()) {
      return "Unknown";
    }
    return String.join(",", this.heiIdsCoveredByClient);
  }

  @Override
  @Transient
  public String getTarget() {
    if (this.apiName != null) {
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
