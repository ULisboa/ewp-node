package pt.ulisboa.ewp.node.domain.repository;

import java.util.Collection;
import java.util.function.Function;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.service.messaging.MessageService;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

@Transactional
public abstract class AbstractRepository<T> {

  private final Logger log = LoggerFactory.getLogger(getClass());
  private final Class<T> entityClass;
  private final SessionFactory sessionFactory;

  protected AbstractRepository(Class<T> entityClass, SessionFactory sessionFactory) {
    this.entityClass = entityClass;
    this.sessionFactory = sessionFactory;
  }

  public Collection<T> findAll() {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = sessionFactory.getCriteriaBuilder();
          CriteriaQuery<T> query = criteriaBuilder.createQuery(entityClass);
          Root<T> selection = query.from(entityClass);
          return session.createQuery(query.select(selection)).getResultList();
        });
  }

  public T merge(T entity) {
    //noinspection unchecked
    return (T) runInSession(session -> session.merge(entity));
  }

  public boolean persist(T entity) {
    try {
      checkDomainConstraints(entity);
    } catch (DomainException e) {
      log.error("Failed to persist entity", e);
      MessageService.getInstance().add(Severity.ERROR, e.getMessage());
      return false;
    }

    return runInSession(
        session -> {
          session.saveOrUpdate(entity);
          return true;
        });
  }

  public boolean delete(T entity) {
    return runInSession(
        session -> {
          session.delete(entity);
          return true;
        });
  }

  protected abstract boolean checkDomainConstraints(T entity) throws DomainException;

  protected <U> U runInSession(Function<Session, U> function) {
    Session session = sessionFactory.getCurrentSession();
    return function.apply(session);
  }
}
