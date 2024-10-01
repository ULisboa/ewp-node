package pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp;

import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import java.io.IOException;
import java.time.ZonedDateTime;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;

@Entity
@DiscriminatorValue("EWP")
public abstract class EwpHttpCommunicationLog extends HttpCommunicationLog {

  private EwpAuthenticationMethod authenticationMethod;

  public EwpHttpCommunicationLog() {
  }

  public EwpHttpCommunicationLog(
      EwpAuthenticationMethod authenticationMethod,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication) throws IOException {
    super(request, response, startProcessingDateTime, endProcessingDateTime, observations,
        parentCommunication);
    this.authenticationMethod = authenticationMethod;
  }

  @Enumerated(EnumType.STRING)
  @Column(name = "authentication_method")
  public EwpAuthenticationMethod getAuthenticationMethod() {
    return authenticationMethod;
  }

  public void setAuthenticationMethod(EwpAuthenticationMethod authenticationMethod) {
    this.authenticationMethod = authenticationMethod;
  }
}
