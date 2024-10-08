package pt.ulisboa.ewp.node.domain.repository.communication.log.http.host;

import jakarta.transaction.Transactional;
import java.io.IOException;
import java.time.ZonedDateTime;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationFromHostLog;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class HttpCommunicationFromHostLogRepository
    extends AbstractRepository<HttpCommunicationFromHostLog> {

  @Autowired
  @Lazy
  private MessageResolver messages;

  protected HttpCommunicationFromHostLogRepository(SessionFactory sessionFactory) {
    super(HttpCommunicationFromHostLog.class, sessionFactory);
  }

  public boolean persist(HttpCommunicationFromHostLog communicationLog) {
    return super.persist(communicationLog);
  }

  public HttpCommunicationFromHostLog create(
      Host host,
      HostForwardEwpApiClient hostForwardEwpApiClient,
      String apiName,
      Integer apiMajorVersion,
      String endpointName,
      String targetHeiId,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication)
      throws DomainException, IOException {
    HttpCommunicationFromHostLog communicationFromHostLog =
        new HttpCommunicationFromHostLog(
            host,
            hostForwardEwpApiClient,
            apiName,
            apiMajorVersion,
            endpointName,
            targetHeiId,
            request,
            response,
            startProcessingDateTime,
            endProcessingDateTime,
            observations,
            parentCommunication);

    if (persist(communicationFromHostLog)) {
      return communicationFromHostLog;
    } else {
      throw new DomainException("Failed to create communication log");
    }
  }

  @Override
  protected boolean checkDomainConstraints(HttpCommunicationFromHostLog entity)
      throws DomainException {
    if (entity.getRequest() == null) {
      throw new DomainException(
          messages.get("error.http.communication.from.host.log.request.must.be.defined"));
    }

    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.http.communication.from.host.log.start.processing.date.time.must.be.defined"));
    }

    return true;
  }
}
