package pt.ulisboa.ewp.node.domain.repository.host.forward.ewp.client;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Root;
import jakarta.transaction.Transactional;
import java.util.Optional;
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

  public Optional<HostForwardEwpApiClient> findByIdAndActive(String id, boolean active) {
    return runInSession(
        session -> {
          CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
          CriteriaQuery<HostForwardEwpApiClient> query = criteriaBuilder.createQuery(
              HostForwardEwpApiClient.class);
          Root<HostForwardEwpApiClient> selection = query.from(HostForwardEwpApiClient.class);
          return session
              .createQuery(query.where(
                  criteriaBuilder.and(
                      criteriaBuilder.equal(selection.get(HostForwardEwpApiClient_.id), id),
                      criteriaBuilder.equal(selection.get(HostForwardEwpApiClient_.active), active)
                  )))
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
