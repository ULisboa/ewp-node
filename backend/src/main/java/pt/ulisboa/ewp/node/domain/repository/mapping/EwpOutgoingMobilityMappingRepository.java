package pt.ulisboa.ewp.node.domain.repository.mapping;

import com.google.common.base.Strings;
import java.util.Optional;
import javax.persistence.NoResultException;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Repository;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping_;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
public class EwpOutgoingMobilityMappingRepository extends
    AbstractRepository<EwpOutgoingMobilityMapping> {

  @Autowired
  @Lazy
  private MessageResolver messages;

  protected EwpOutgoingMobilityMappingRepository(SessionFactory sessionFactory) {
    super(EwpOutgoingMobilityMapping.class, sessionFactory);
  }

  public Optional<EwpOutgoingMobilityMapping> findByHeiIdAndOmobilityId(String heiId,
      String omobilityId) {
    return runInSession(
        session -> {
          try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<EwpOutgoingMobilityMapping> query = criteriaBuilder.createQuery(
                EwpOutgoingMobilityMapping.class);
            Root<EwpOutgoingMobilityMapping> selection = query.from(
                EwpOutgoingMobilityMapping.class);
            return Optional.ofNullable(
                session
                    .createQuery(
                        query.where(
                            criteriaBuilder.equal(
                                selection.get(EwpOutgoingMobilityMapping_.heiId), heiId),
                            criteriaBuilder.equal(
                                selection.get(EwpOutgoingMobilityMapping_.omobilityId),
                                omobilityId)))
                    .getSingleResult());

          } catch (NoResultException ignored) {
            return Optional.empty();
          }
        });
  }

  @Override
  protected boolean checkDomainConstraints(EwpOutgoingMobilityMapping entity)
      throws DomainException {
    if (Strings.isNullOrEmpty(entity.getHeiId())) {
      throw new DomainException(
          messages.get("error.outgoingMobilityMapping.heiId.must.be.defined"));
    }

    if (Strings.isNullOrEmpty(entity.getOmobilityId())) {
      throw new DomainException(
          messages.get("error.outgoingMobilityMapping.omobilityId.must.be.defined"));
    }

    if (findAll().stream().anyMatch(o -> o != entity && o.getHeiId().equals(entity.getHeiId()) &&
        o.getOmobilityId().equals(entity.getOmobilityId()))) {
      messages.get("error.outgoingMobilityMapping.must.be.unique");
    }

    return true;
  }
}
