package pt.ulisboa.ewp.node.domain.entity.http.log.ewp;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog;

@Entity
@DiscriminatorValue("EWP")
public abstract class EwpHttpCommunicationLog extends HttpCommunicationLog {

  private EwpAuthenticationMethod authenticationMethod;

  public EwpHttpCommunicationLog() {}

  public EwpHttpCommunicationLog(
      EwpAuthenticationMethod authenticationMethod,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations) {
    super(request, response, startProcessingDateTime, endProcessingDateTime, observations);
    this.authenticationMethod = authenticationMethod;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "authentication_method", nullable = false)
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return authenticationMethod;
  }

  public void setAuthenticationMethod(EwpAuthenticationMethod authenticationMethod) {
    this.authenticationMethod = authenticationMethod;
  }
}
