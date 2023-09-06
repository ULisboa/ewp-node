package pt.ulisboa.ewp.node.domain.entity.communication.log.http;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.persistence.*;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;

@Entity
@DiscriminatorValue("HTTP")
public abstract class HttpCommunicationLog extends CommunicationLog {
  private HttpRequestLog request;
  private HttpResponseLog response;

  protected HttpCommunicationLog() {
  }

  protected HttpCommunicationLog(
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      CommunicationLog parentCommunication) throws IOException {
    super(startProcessingDateTime, endProcessingDateTime, observations, parentCommunication);
    this.request = request;
    this.response = response;
  }

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "request_log")
  public HttpRequestLog getRequest() {
    return request;
  }

  public void setRequest(HttpRequestLog request) {
    this.request = request;
  }

  @OneToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
  @JoinColumn(name = "response_log")
  public HttpResponseLog getResponse() {
    return response;
  }

  public void setResponse(HttpResponseLog response) {
    this.response = response;
    if (this.response != null) {
      if (HttpStatus.valueOf(this.response.getStatusCode()).isError()) {
        this.markAsFailure();
      } else {
        this.markAsSuccess();
      }
    }
  }

  @Override
  @Transient
  public String getTarget() {
    return request.getUrl();
  }
}
