package pt.ulisboa.ewp.node.domain.repository.communication.log.http;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.http.HttpCommunicationLog_;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class HttpCommunicationLogRepository
    extends AbstractRepository<HttpCommunicationLog> {

  @Autowired
  @Lazy
  private MessageResolver messages;

  protected HttpCommunicationLogRepository(SessionFactory sessionFactory) {
    super(HttpCommunicationLog.class, sessionFactory);
  }

  public Optional<HttpCommunicationLog> findById(Long id) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<HttpCommunicationLog> query = criteriaBuilder.createQuery(
              HttpCommunicationLog.class);
          Root<HttpCommunicationLog> selection = query.from(HttpCommunicationLog.class);
          return session
              .createQuery(
                  query.where(criteriaBuilder.equal(selection.get(HttpCommunicationLog_.id), id)))
              .stream()
              .findFirst();
        });
  }

  @Override
  protected boolean checkDomainConstraints(HttpCommunicationLog entity)
      throws DomainException {
    if (entity.getRequest() == null) {
      throw new DomainException(
          messages.get("error.http.communication.log.request.must.be.defined"));
    }

    if (entity.getResponse() == null) {
      throw new DomainException(
          messages.get("error.http.communication.log.response.must.be.defined"));
    }

    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.http.communication.log.start.processing.date.time.must.be.defined"));
    }

    return true;
  }
}
