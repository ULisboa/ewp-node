package pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collection;
import javax.persistence.CollectionTable;
import javax.persistence.DiscriminatorValue;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;

@Entity
@DiscriminatorValue("EWP_IN")
public class HttpCommunicationFromEwpNodeLog extends EwpHttpCommunicationLog {

  private Collection<String> heiIdsCoveredByClient;

  public HttpCommunicationFromEwpNodeLog() {}

  public HttpCommunicationFromEwpNodeLog(
      EwpAuthenticationMethod authenticationMethod,
      Collection<String> heiIdsCoveredByClient,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication) throws IOException {
    super(
        authenticationMethod,
        request,
        response,
        startProcessingDateTime,
        endProcessingDateTime,
        observations,
        parentCommunication);
    this.heiIdsCoveredByClient = heiIdsCoveredByClient;
  }

  @ElementCollection
  @CollectionTable(name = "COMMUNICATION_FROM_EWP_NODE_LOG_HEI_IDS_COVERED_BY_CLIENT")
  public Collection<String> getHeiIdsCoveredByClient() {
    return heiIdsCoveredByClient;
  }

  public void setHeiIdsCoveredByClient(Collection<String> heiIdsCoveredByClient) {
    this.heiIdsCoveredByClient = heiIdsCoveredByClient;
  }
}
