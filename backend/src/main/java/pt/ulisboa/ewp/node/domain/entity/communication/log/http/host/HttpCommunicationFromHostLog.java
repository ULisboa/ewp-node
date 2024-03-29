package pt.ulisboa.ewp.node.domain.entity.communication.log.http.host;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Transient;
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

  public HttpCommunicationFromHostLog() {
  }

  public HttpCommunicationFromHostLog(
      Host host,
      HostForwardEwpApiClient hostForwardEwpApiClient,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication) throws IOException {
    super(
        host,
        request,
        response,
        startProcessingDateTime,
        endProcessingDateTime,
        observations,
        parentCommunication);
    this.hostForwardEwpApiClient = hostForwardEwpApiClient;
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

  @Override
  @Transient
  public String getSource() {
    if (getHostForwardEwpApiClient() == null) {
      return "Unknown";
    }
    return getHostForwardEwpApiClient().getId();
  }
}
