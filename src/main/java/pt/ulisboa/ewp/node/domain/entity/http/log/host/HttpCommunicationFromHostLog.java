package pt.ulisboa.ewp.node.domain.entity.http.log.host;

import java.time.ZonedDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;

@Entity
@DiscriminatorValue("HOST_IN")
public class HttpCommunicationFromHostLog extends HostHttpCommunicationLog {

  public HttpCommunicationFromHostLog() {}

  public HttpCommunicationFromHostLog(
      Host host,
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
  }
}
