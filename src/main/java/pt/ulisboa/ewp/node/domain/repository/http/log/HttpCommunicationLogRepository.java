package pt.ulisboa.ewp.node.domain.repository.http.log;

import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.http.log.HttpCommunicationLog_;
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
