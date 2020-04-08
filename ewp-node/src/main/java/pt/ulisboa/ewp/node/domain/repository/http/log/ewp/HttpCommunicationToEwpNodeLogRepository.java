package pt.ulisboa.ewp.node.domain.repository.http.log.ewp;

import java.time.ZonedDateTime;

import javax.transaction.Transactional;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;

import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class HttpCommunicationToEwpNodeLogRepository
    extends AbstractRepository<HttpCommunicationToEwpNodeLog> {

  @Autowired @Lazy private MessageResolver messages;

  protected HttpCommunicationToEwpNodeLogRepository(SessionFactory sessionFactory) {
    super(HttpCommunicationToEwpNodeLog.class, sessionFactory);
  }

  public boolean create(
      EwpAuthenticationMethod authenticationMethod,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations) {
    HttpCommunicationToEwpNodeLog communicationToEwpNodeLog =
        new HttpCommunicationToEwpNodeLog(
            authenticationMethod,
            request,
            response,
            startProcessingDateTime,
            endProcessingDateTime,
            observations);
    return persist(communicationToEwpNodeLog);
  }

  @Override
  protected boolean checkDomainConstraints(HttpCommunicationToEwpNodeLog entity)
      throws DomainException {
    if (entity.getRequest() == null) {
      throw new DomainException(
          messages.get("error.http.communication.to.ewp.node.log.request.must.be.defined"));
    }

    if (entity.getResponse() == null) {
      throw new DomainException(
          messages.get("error.http.communication.to.ewp.node.log.response.must.be.defined"));
    }

    return true;
  }
}
