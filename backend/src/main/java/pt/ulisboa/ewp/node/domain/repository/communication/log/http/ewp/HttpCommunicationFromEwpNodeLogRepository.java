package pt.ulisboa.ewp.node.domain.repository.communication.log.http.ewp;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationFromEwpNodeLog_;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog_;
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

  public Optional<HttpCommunicationFromEwpNodeLog> findById(Long id) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<HttpCommunicationFromEwpNodeLog> query =
              criteriaBuilder.createQuery(HttpCommunicationFromEwpNodeLog.class);
          Root<HttpCommunicationFromEwpNodeLog> selection =
              query.from(HttpCommunicationFromEwpNodeLog.class);
          return session
              .createQuery(
                  query.where(
                      criteriaBuilder.equal(
                          selection.get(HttpCommunicationFromEwpNodeLog_.id), id)))
              .stream()
              .findFirst();
        });
  }

  public HttpCommunicationFromEwpNodeLog create(
      EwpAuthenticationMethod authenticationMethod,
      Collection<String> coveredHeiIds,
      String apiName,
      Integer apiMajorVersion,
      String endpointName,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String observations,
      HttpCommunicationLog parentCommunication)
      throws IOException, DomainException {
    HttpCommunicationFromEwpNodeLog communicationFromEwpNodeLog =
        new HttpCommunicationFromEwpNodeLog(
            authenticationMethod,
            coveredHeiIds,
            apiName,
            apiMajorVersion,
            endpointName,
            request,
            response,
            startProcessingDateTime,
            endProcessingDateTime,
            observations,
            parentCommunication);

    if (persist(communicationFromEwpNodeLog)) {
      return communicationFromEwpNodeLog;
    } else {
      throw new DomainException("Failed to create communication log");
    }
  }

  @Override
  protected boolean checkDomainConstraints(HttpCommunicationFromEwpNodeLog entity)
      throws DomainException {
    if (entity.getRequest() == null) {
      throw new DomainException(
          messages.get("error.http.communication.from.ewp.node.log.request.must.be.defined"));
    }

    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get("error.http.communication.from.ewp.node.log.start.processing.date.time.must.be.defined"));
    }

    return true;
  }
}
