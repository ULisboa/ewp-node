package pt.ulisboa.ewp.node.domain.repository.host.forward.ewp.client;

import java.util.Optional;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.transaction.Transactional;
import org.hibernate.SessionFactory;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient;
import pt.ulisboa.ewp.node.domain.entity.api.host.forward.ewp.client.HostForwardEwpApiClient_;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;

@Repository
@Transactional
public class HostForwardEwpApiClientRepository extends AbstractRepository<HostForwardEwpApiClient> {

  protected HostForwardEwpApiClientRepository(SessionFactory sessionFactory) {
    super(HostForwardEwpApiClient.class, sessionFactory);
  }

  public Optional<HostForwardEwpApiClient> findById(String id) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<HostForwardEwpApiClient> query = criteriaBuilder.createQuery(
              HostForwardEwpApiClient.class);
          Root<HostForwardEwpApiClient> selection = query.from(HostForwardEwpApiClient.class);
          return session
              .createQuery(query.where(
                  criteriaBuilder.equal(selection.get(HostForwardEwpApiClient_.id), id)))
              .stream()
              .findFirst();
        });
  }

  @Override
  protected boolean checkDomainConstraints(HostForwardEwpApiClient hostForwardEwpApiClient)
      throws DomainException {
    return true;
  }
}
