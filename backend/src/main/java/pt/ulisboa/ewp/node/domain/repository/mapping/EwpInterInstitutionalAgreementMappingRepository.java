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
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping_;
import pt.ulisboa.ewp.node.domain.repository.AbstractRepository;
import pt.ulisboa.ewp.node.exception.domain.DomainException;
import pt.ulisboa.ewp.node.utils.i18n.MessageResolver;

@Repository
public class EwpInterInstitutionalAgreementMappingRepository extends
    AbstractRepository<EwpInterInstitutionalAgreementMapping> {

  @Autowired
  @Lazy
  private MessageResolver messages;

  protected EwpInterInstitutionalAgreementMappingRepository(SessionFactory sessionFactory) {
    super(EwpInterInstitutionalAgreementMapping.class, sessionFactory);
  }

  public Optional<EwpInterInstitutionalAgreementMapping> findByHeiIdAndIiaId(String heiId,
      String iiaId) {
    return runInSession(
        session -> {
          try {
            CriteriaBuilder criteriaBuilder = session.getCriteriaBuilder();
            CriteriaQuery<EwpInterInstitutionalAgreementMapping> query = criteriaBuilder.createQuery(
                EwpInterInstitutionalAgreementMapping.class);
            Root<EwpInterInstitutionalAgreementMapping> selection = query.from(
                EwpInterInstitutionalAgreementMapping.class);
            return Optional.ofNullable(
                session
                    .createQuery(
                        query.where(
                            criteriaBuilder.equal(
                                selection.get(EwpInterInstitutionalAgreementMapping_.heiId), heiId),
                            criteriaBuilder.equal(
                                selection.get(EwpInterInstitutionalAgreementMapping_.iiaId),
                                iiaId)))
                    .getSingleResult());

          } catch (NoResultException ignored) {
            return Optional.empty();
          }
        });
  }

  @Override
  protected boolean checkDomainConstraints(EwpInterInstitutionalAgreementMapping entity)
      throws DomainException {
    if (Strings.isNullOrEmpty(entity.getHeiId())) {
      throw new DomainException(
          messages.get("error.interInstitutionalAgreementMapping.heiId.must.be.defined"));
    }

    if (Strings.isNullOrEmpty(entity.getIiaId())) {
      throw new DomainException(
          messages.get("error.interInstitutionalAgreementMapping.iiaId.must.be.defined"));
    }

    if (findAll().stream().anyMatch(o -> o != entity && o.getHeiId().equals(entity.getHeiId()) &&
        o.getIiaId().equals(entity.getIiaId()))) {
      messages.get("error.interInstitutionalAgreementMapping.must.be.unique");
    }

    return true;
  }
}
