package pt.ulisboa.ewp.node.domain.entity.http.log.host;

import java.time.ZonedDateTime;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog;

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
      HttpCommunicationLog parentCommunication) {
    super(
        host,
        request,
        response,
        startProcessingDateTime,
        endProcessingDateTime,
        observations, parentCommunication);
  }
}
