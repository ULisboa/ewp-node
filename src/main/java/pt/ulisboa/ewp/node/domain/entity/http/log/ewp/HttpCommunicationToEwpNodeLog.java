package pt.ulisboa.ewp.node.domain.entity.http.log.ewp;

import java.time.ZonedDateTime;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;

@Entity
@DiscriminatorValue("EWP_OUT")
public class HttpCommunicationToEwpNodeLog extends EwpHttpCommunicationLog {

  public HttpCommunicationToEwpNodeLog() {}

  public HttpCommunicationToEwpNodeLog(
      EwpAuthenticationMethod authenticationMethod,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations) {
    super(
        authenticationMethod,
        request,
        response,
        startProcessingDateTime,
        endProcessingDateTime,
        observations);
  }
}
