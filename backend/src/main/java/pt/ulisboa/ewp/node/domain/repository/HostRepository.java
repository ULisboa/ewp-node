package pt.ulisboa.ewp.node.domain.repository;

import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.Subquery;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.Hei;
import pt.ulisboa.ewp.node.domain.entity.Hei_;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.entity.Host_;
import pt.ulisboa.ewp.node.exception.domain.DomainException;

@Repository
@Transactional
public class HostRepository extends AbstractRepository<Host> {

  protected HostRepository(SessionFactory sessionFactory) {
    super(Host.class, sessionFactory);
  }

  public Optional<Host> findByCoveredHeiId(String heiId) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<Host> query = criteriaBuilder.createQuery(Host.class);
          Root<Host> root = query.from(Host.class);

          Subquery<String> schacCodeSubQuery = query.subquery(String.class);
          Root<Hei> heiRoot = schacCodeSubQuery.from(Hei.class);
          Join<Host, Hei> heiJoin = root.join(Host_.coveredHeis);
          schacCodeSubQuery.select(heiRoot.get(Hei_.schacCode));

          schacCodeSubQuery.where(criteriaBuilder.equal(heiJoin.get(Hei_.schacCode), heiId));

          return session
              .createQuery(
                  query.where(
                      criteriaBuilder.in(heiJoin.get(Hei_.schacCode)).value(schacCodeSubQuery)))
              .stream()
              .findFirst();
        });
  }

  public Optional<Host> findByCode(String code) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<Host> query = criteriaBuilder.createQuery(Host.class);
          Root<Host> selection = query.from(Host.class);
          return session
              .createQuery(query.where(criteriaBuilder.equal(selection.get(Host_.code), code)))
              .stream()
              .findFirst();
        });
  }

  @Override
  protected boolean checkDomainConstraints(Host host) throws DomainException {
    return true;
  }
}
