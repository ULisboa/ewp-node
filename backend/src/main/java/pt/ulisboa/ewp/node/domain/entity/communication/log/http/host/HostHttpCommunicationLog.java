package pt.ulisboa.ewp.node.domain.entity.communication.log.http.host;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import java.io.IOException;
import java.time.ZonedDateTime;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;

@Entity
@DiscriminatorValue("HOST")
public abstract class HostHttpCommunicationLog extends HttpCommunicationLog {

  private Host host;

  public HostHttpCommunicationLog() {}

  public HostHttpCommunicationLog(
      Host host,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication) throws IOException {
    super(request, response, startProcessingDateTime, endProcessingDateTime, observations,
        parentCommunication);
    this.host = host;
  }

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "host_id")
  public Host getHost() {
    return host;
  }

  public void setHost(Host host) {
    this.host = host;
  }
}
