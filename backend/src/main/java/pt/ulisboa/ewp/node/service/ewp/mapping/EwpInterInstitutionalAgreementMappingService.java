package pt.ulisboa.ewp.node.service.ewp.mapping;

import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;

@Service
@Transactional
public class EwpInterInstitutionalAgreementMappingService {

  private final EwpInterInstitutionalAgreementMappingRepository repository;

  public EwpInterInstitutionalAgreementMappingService(
      EwpInterInstitutionalAgreementMappingRepository repository) {
    this.repository = repository;
  }

  public Optional<EwpInterInstitutionalAgreementMapping> getMapping(String heiId, String iiaId) {
    return repository.findByHeiIdAndIiaId(heiId, iiaId);
  }

  @Transactional
  public void registerMapping(String heiId, String ounitId, String iiaId) {
    Optional<EwpInterInstitutionalAgreementMapping> mappingOptional = repository.findByHeiIdAndIiaId(
        heiId, iiaId);

    EwpInterInstitutionalAgreementMapping mapping;
    if (mappingOptional.isEmpty()) {
      mapping = EwpInterInstitutionalAgreementMapping.create(heiId, ounitId, iiaId);

    } else {
      mapping = mappingOptional.get();
      mapping.setOunitId(ounitId);
    }

    if (!repository.persist(mapping)) {
      throw new IllegalStateException();
    }
  }
}
