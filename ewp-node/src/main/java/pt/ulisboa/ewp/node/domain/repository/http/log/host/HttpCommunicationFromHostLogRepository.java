package pt.ulisboa.ewp.node.domain.repository.http.log.host;

import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.host.HttpCommunicationFromHostLog;
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

  public boolean create(
      Host host,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations) {
    HttpCommunicationFromHostLog communicationFromHostLog =
        new HttpCommunicationFromHostLog(
            host,
            request,
            response,
            startProcessingDateTime,
            endProcessingDateTime,
            observations);
    return persist(communicationFromHostLog);
  }

  @Override
  protected boolean checkDomainConstraints(HttpCommunicationFromHostLog entity)
      throws DomainException {
    if (entity.getRequest() == null) {
      throw new DomainException(
          messages.get("error.http.communication.from.host.log.request.must.be.defined"));
    }

    if (entity.getResponse() == null) {
      throw new DomainException(
          messages.get("error.http.communication.from.host.log.response.must.be.defined"));
    }

    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.http.communication.from.host.log.start.processing.date.time.must.be.defined"));
    }

    if (entity.getEndProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.http.communication.from.host.log.end.processing.date.time.must.be.defined"));
    }

    return true;
  }
}
