package pt.ulisboa.ewp.node.domain.entity.communication.log.http.host;

import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import java.io.IOException;
import java.time.ZonedDateTime;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;

@Entity
@DiscriminatorValue(HttpCommunicationToHostLog.TYPE)
public class HttpCommunicationToHostLog extends HostHttpCommunicationLog {

  public static final String TYPE = "HOST_OUT";

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

  @Override
  @Transient
  public String getSource() {
    String superSource = super.getSource();
    if (superSource != null && !superSource.equals("Unknown")) {
      return superSource;
    }
    return "EWP Node";
  }

  @Override
  @Transient
  public String getTarget() {
    if (getHost() == null) {
      return "Unknown";
    }
    return getHost().getCode();
  }
}
