package pt.ulisboa.ewp.node.domain.entity.http.log.host;

import java.time.ZonedDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog;

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
      String observations) {
    super(request, response, startProcessingDateTime, endProcessingDateTime, observations);
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
