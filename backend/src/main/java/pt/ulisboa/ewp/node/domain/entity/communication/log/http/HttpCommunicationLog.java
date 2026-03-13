package pt.ulisboa.ewp.node.domain.entity.communication.log.http;

import jakarta.persistence.CascadeType;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Transient;
import java.io.IOException;
import java.time.ZonedDateTime;
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
    Status status = super.getStatus();
    if (status == Status.SUCCESS) {
      if (isResponseStatusAccepted()) {
        return Status.ACCEPTED;
      } else if (isResponseStatusError()) {
        return Status.FAILURE;
      }
      return Status.SUCCESS;
    }
    return status;
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
  private boolean isResponseStatusAccepted() {
    return HttpStatus.valueOf(this.response.getStatusCode()).equals(HttpStatus.ACCEPTED);
  }

  @Transient
  private boolean isResponseStatusError() {
    return HttpStatus.valueOf(this.response.getStatusCode()).isError();
  }
}
