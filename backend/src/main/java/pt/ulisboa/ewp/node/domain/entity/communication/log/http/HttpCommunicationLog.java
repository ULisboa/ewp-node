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

  @Override
  public Status getStatus() {
    switch (super.getStatus()) {
      case SUCCESS:
        if (isResponseStatusError()) {
          return Status.FAILURE;
        }
        return Status.SUCCESS;

      case INCOMPLETE:
        return Status.INCOMPLETE;

      case FAILURE:
        return Status.FAILURE;

      default:
        throw new IllegalStateException("Unknown status: " + super.getStatus());
    }
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
  }

  @Override
  @Transient
  public String getTarget() {
    return request.getUrl();
  }

  @Transient
  private boolean isResponseStatusError() {
    return HttpStatus.valueOf(this.response.getStatusCode()).isError();
  }
}
