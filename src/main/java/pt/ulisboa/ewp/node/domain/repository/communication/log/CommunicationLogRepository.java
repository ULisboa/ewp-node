package pt.ulisboa.ewp.node.domain.repository.communication.log;

import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog_;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class CommunicationLogRepository
    extends AbstractRepository<CommunicationLog> {

  @Autowired
  @Lazy
  private MessageResolver messages;

  protected CommunicationLogRepository(SessionFactory sessionFactory) {
    super(CommunicationLog.class, sessionFactory);
  }

  public Optional<CommunicationLog> findById(Long id) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<CommunicationLog> query = criteriaBuilder.createQuery(
                  CommunicationLog.class);
          Root<CommunicationLog> selection = query.from(CommunicationLog.class);
          return session
              .createQuery(
                  query.where(criteriaBuilder.equal(selection.get(CommunicationLog_.id), id)))
              .stream()
              .findFirst();
        });
  }

  @Override
  protected boolean checkDomainConstraints(CommunicationLog entity)
      throws DomainException {
    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get(
              "error.http.communication.log.start.processing.date.time.must.be.defined"));
    }

    return true;
  }
}
