package pt.ulisboa.ewp.node.domain.repository.http.log.ewp;

import java.time.ZonedDateTime;
import java.util.Collection;

import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class HttpCommunicationFromEwpNodeLogRepository
    extends AbstractRepository<HttpCommunicationFromEwpNodeLog> {

  @Autowired @Lazy private MessageResolver messages;

  protected HttpCommunicationFromEwpNodeLogRepository(SessionFactory sessionFactory) {
    super(HttpCommunicationFromEwpNodeLog.class, sessionFactory);
  }

  public boolean create(
      EwpAuthenticationMethod authenticationMethod,
      Collection<String> coveredHeiIds,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations) {
    HttpCommunicationFromEwpNodeLog communicationFromEwpNodeLog =
        new HttpCommunicationFromEwpNodeLog(
            authenticationMethod,
            coveredHeiIds,
            request,
            response,
            startProcessingDateTime,
            endProcessingDateTime,
            observations);
    return persist(communicationFromEwpNodeLog);
  }

  @Override
  protected boolean checkDomainConstraints(HttpCommunicationFromEwpNodeLog entity)
      throws DomainException {
    if (entity.getRequest() == null) {
      throw new DomainException(
          messages.get("error.http.communication.from.ewp.node.log.request.must.be.defined"));
    }

    if (entity.getResponse() == null) {
      throw new DomainException(
          messages.get("error.http.communication.from.ewp.node.log.response.must.be.defined"));
    }

    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get("error.http.communication.from.ewp.node.log.start.processing.date.time.must.be.defined"));
    }

    if (entity.getEndProcessingDateTime() == null) {
      throw new DomainException(
          messages.get("error.http.communication.from.ewp.node.log.end.processing.date.time.must.be.defined"));
    }

    return true;
  }
}
