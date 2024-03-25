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
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpRequestLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpResponseLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.ewp.HttpCommunicationToEwpNodeLog_;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.ExceptionUtils;
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

  public Optional<HttpCommunicationToEwpNodeLog> findById(Long id) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<HttpCommunicationToEwpNodeLog> query =
              criteriaBuilder.createQuery(HttpCommunicationToEwpNodeLog.class);
          Root<HttpCommunicationToEwpNodeLog> selection = query.from(HttpCommunicationToEwpNodeLog.class);
          return session
              .createQuery(
                  query.where(criteriaBuilder.equal(selection.get(HttpCommunicationToEwpNodeLog_.id), id)))
              .stream()
              .findFirst();
        });
  }

  public HttpCommunicationToEwpNodeLog create(
      String targetHeiId,
      String apiName,
      String apiVersion,
      String endpointName,
      EwpAuthenticationMethod authenticationMethod,
      HttpRequestLog request,
      HttpResponseLog response,
      ZonedDateTime startProcessingDateTime,
      ZonedDateTime endProcessingDateTime,
      String serverDeveloperMessage,
      String observations,
      HttpCommunicationLog parentCommunication,
      Collection<EwpChangeNotification> ewpChangeNotifications,
      EwpClientErrorException ewpClientErrorException)
      throws IOException {
    HttpCommunicationToEwpNodeLog communicationToEwpNodeLog =
        new HttpCommunicationToEwpNodeLog(
            targetHeiId,
            apiName,
            apiVersion,
            endpointName,
            authenticationMethod,
            request,
            response,
            startProcessingDateTime,
            endProcessingDateTime,
            serverDeveloperMessage,
            observations,
            parentCommunication,
            ewpChangeNotifications);
    if (ewpClientErrorException != null) {
      communicationToEwpNodeLog.setExceptionStacktrace(
          ExceptionUtils.getStackTraceAsString(
              ewpClientErrorException, CommunicationLog.MAX_NUMBER_OF_STACK_TRACE_LINES_PER_LEVEL));
    }
    if (!persist(communicationToEwpNodeLog)) {
      throw new IllegalStateException("Failed to register communication");
    }
    return communicationToEwpNodeLog;
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
