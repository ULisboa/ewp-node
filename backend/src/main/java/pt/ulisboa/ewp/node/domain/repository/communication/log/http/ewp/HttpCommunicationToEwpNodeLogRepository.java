package pt.ulisboa.ewp.node.domain.repository.communication.log.http.ewp;

import java.io.IOException;
import java.time.ZonedDateTime;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class HttpCommunicationToEwpNodeLogRepository
    extends AbstractRepository<HttpCommunicationToEwpNodeLog> {

  @Autowired
  @Lazy
  private MessageResolver messages;

  protected HttpCommunicationToEwpNodeLogRepository(SessionFactory sessionFactory) {
    super(HttpCommunicationToEwpNodeLog.class, sessionFactory);
  }

  public boolean create(
      String targetHeiId,
      String apiName,
      String apiVersion,
      EwpAuthenticationMethod authenticationMethod,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication) throws IOException {
    HttpCommunicationToEwpNodeLog communicationToEwpNodeLog =
        new HttpCommunicationToEwpNodeLog(
            targetHeiId,
            apiName,
            apiVersion,
            authenticationMethod,
            request,
            response,
            startProcessingDateTime,
            endProcessingDateTime,
            observations, parentCommunication);
    return persist(communicationToEwpNodeLog);
  }

  @Override
  protected boolean checkDomainConstraints(HttpCommunicationToEwpNodeLog entity)
      throws DomainException {
    if (entity.getRequest() == null) {
      throw new DomainException(
          messages.get("error.http.communication.to.ewp.node.log.request.must.be.defined"));
    }

    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.http.communication.to.ewp.node.log.start.processing.date.time.must.be.defined"));
    }

    if (entity.getEndProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.http.communication.to.ewp.node.log.end.processing.date.time.must.be.defined"));
    }

    return true;
  }
}
