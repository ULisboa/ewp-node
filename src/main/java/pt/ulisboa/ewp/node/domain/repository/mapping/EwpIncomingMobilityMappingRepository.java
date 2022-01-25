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
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpIncomingMobilityMapping;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpIncomingMobilityMapping_;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
public class EwpIncomingMobilityMappingRepository extends
    AbstractRepository<EwpIncomingMobilityMapping> {

  @Autowired
  @Lazy
  private MessageResolver messages;

  protected EwpIncomingMobilityMappingRepository(SessionFactory sessionFactory) {
    super(EwpIncomingMobilityMapping.class, sessionFactory);
  }

  public Optional<EwpIncomingMobilityMapping> findByReceivingHeiIdAndOmobilityId(String rceivingHeiId,
      String omobilityId) {
    return runInSession(
        session -> {
          try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<EwpIncomingMobilityMapping> query = criteriaBuilder.createQuery(
                EwpIncomingMobilityMapping.class);
            Root<EwpIncomingMobilityMapping> selection = query.from(
                EwpIncomingMobilityMapping.class);
            return Optional.ofNullable(
                session
                    .createQuery(
                        query.where(
                            criteriaBuilder.equal(
                                selection.get(EwpIncomingMobilityMapping_.receivingHeiId), rceivingHeiId),
                            criteriaBuilder.equal(
                                selection.get(EwpIncomingMobilityMapping_.omobilityId),
                                omobilityId)))
                    .getSingleResult());

          } catch (NoResultException ignored) {
            return Optional.empty();
          }
        });
  }

  @Override
  protected boolean checkDomainConstraints(EwpIncomingMobilityMapping entity)
      throws DomainException {
    if (Strings.isNullOrEmpty(entity.getReceivingOunitId())) {
      throw new DomainException(
          messages.get("error.incomingMobilityMapping.receivingHeiId.must.be.defined"));
    }

    if (Strings.isNullOrEmpty(entity.getOmobilityId())) {
      throw new DomainException(
          messages.get("error.incomingMobilityMapping.omobilityId.must.be.defined"));
    }

    if (findAll().stream().anyMatch(o -> o != entity && o.getReceivingHeiId().equals(entity.getReceivingHeiId()) &&
        o.getOmobilityId().equals(entity.getOmobilityId()))) {
      messages.get("error.incomingMobilityMapping.must.be.unique");
    }

    return true;
  }
}
