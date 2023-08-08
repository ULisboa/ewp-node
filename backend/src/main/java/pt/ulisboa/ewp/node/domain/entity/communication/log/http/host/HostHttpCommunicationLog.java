package pt.ulisboa.ewp.node.domain.entity.communication.log.http.host;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
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
