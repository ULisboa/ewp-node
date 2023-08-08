package pt.ulisboa.ewp.node.domain.entity.communication.log.http.host;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;

@Entity
@DiscriminatorValue("HOST_OUT")
public class HttpCommunicationToHostLog extends HostHttpCommunicationLog {

  public HttpCommunicationToHostLog() {}

  public HttpCommunicationToHostLog(
      Host host,
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
        observations, parentCommunication);
  }
}
