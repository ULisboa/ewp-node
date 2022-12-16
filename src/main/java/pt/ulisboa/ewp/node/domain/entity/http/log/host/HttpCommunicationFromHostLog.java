package pt.ulisboa.ewp.node.domain.entity.http.log.host;

import java.time.ZonedDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;

@Entity
@DiscriminatorValue("HOST_IN")
public class HttpCommunicationFromHostLog extends HostHttpCommunicationLog {

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
      String observations) {
    super(
        host,
        request,
        response,
        startProcessingDateTime,
        endProcessingDateTime,
        observations);
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

}
