package pt.ulisboa.ewp.node.domain.repository.communication.log.http.host;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.host.HttpCommunicationToHostLog;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class HttpCommunicationToHostLogRepository
    extends AbstractRepository<HttpCommunicationToHostLog> {

  @Autowired
  @Lazy
  private MessageResolver messages;

  protected HttpCommunicationToHostLogRepository(SessionFactory sessionFactory) {
    super(HttpCommunicationToHostLog.class, sessionFactory);
  }

  public boolean create(
      Host host,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication) throws IOException {
    HttpCommunicationToHostLog communicationToHostLog =
        new HttpCommunicationToHostLog(
            host,
            request,
            response,
            startProcessingDateTime,
            endProcessingDateTime,
            observations, parentCommunication);
    return persist(communicationToHostLog);
  }

  @Override
  protected boolean checkDomainConstraints(HttpCommunicationToHostLog entity)
      throws DomainException {
    if (entity.getHost() == null) {
      throw new DomainException(
          messages.get("error.http.communication.to.host.log.host.must.be.defined"));
    }

    if (entity.getRequest() == null) {
      throw new DomainException(
          messages.get("error.http.communication.to.host.log.request.must.be.defined"));
    }

    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.http.communication.to.host.log.start.processing.date.time.must.be.defined"));
    }

    if (entity.getEndProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.http.communication.to.host.log.end.processing.date.time.must.be.defined"));
    }

    return true;
  }
}
