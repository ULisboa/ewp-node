package pt.ulisboa.ewp.node.domain.repository.communication.log;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.util.Collection;
import java.util.Optional;
import java.util.stream.Collectors;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.dto.filter.FilterDto;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog_;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
@Transactional
public class CommunicationLogRepository extends AbstractRepository<CommunicationLog> {

  @Autowired @Lazy private MessageResolver messages;

  protected CommunicationLogRepository(SessionFactory sessionFactory) {
    super(CommunicationLog.class, sessionFactory);
  }

  public Collection<CommunicationLog> findByFilter(FilterDto<CommunicationLog> filter, int offset, int limit) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<CommunicationLog> query =
              criteriaBuilder.createQuery(CommunicationLog.class);
          Root<CommunicationLog> selection = query.from(CommunicationLog.class);
          if (filter != null) {
            query = query.where(filter.createPredicate(criteriaBuilder, selection));
          }
          query.orderBy(criteriaBuilder.desc(selection.get(CommunicationLog_.id)));
          return session.createQuery(query).setFirstResult(offset).setMaxResults(limit).stream()
              .collect(Collectors.toList());
        });
  }

  public long countByFilter(FilterDto<CommunicationLog> filter) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<Long> query = criteriaBuilder.createQuery(Long.class);
          Root<CommunicationLog> selection = query.from(CommunicationLog.class);
          query.select(criteriaBuilder.count(selection));
          if (filter != null) {
            query = query.where(filter.createPredicate(criteriaBuilder, selection));
          }
          return session.createQuery(query).getSingleResult();
        });
  }

  public Optional<CommunicationLog> findById(Long id) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<CommunicationLog> query =
              criteriaBuilder.createQuery(CommunicationLog.class);
          Root<CommunicationLog> selection = query.from(CommunicationLog.class);
          return session
              .createQuery(
                  query.where(criteriaBuilder.equal(selection.get(CommunicationLog_.id), id)))
              .stream()
              .findFirst();
        });
  }

  @Override
  protected boolean checkDomainConstraints(CommunicationLog entity) throws DomainException {
    if (entity.getStartProcessingDateTime() == null) {
      throw new DomainException(
          messages.get("error.http.communication.log.start.processing.date.time.must.be.defined"));
    }

    return true;
  }
}
