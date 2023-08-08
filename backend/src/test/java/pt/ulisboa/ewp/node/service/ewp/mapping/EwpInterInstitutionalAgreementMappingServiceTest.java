package pt.ulisboa.ewp.node.service.ewp.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;

class EwpInterInstitutionalAgreementMappingServiceTest extends AbstractIntegrationTest {

  @Autowired
  private EwpInterInstitutionalAgreementMappingRepository repository;

  @Autowired
  private EwpInterInstitutionalAgreementMappingService service;

  @Test
  void testRegisterMapping_NewMapping_MappingCreated() {
    String heiId = UUID.randomUUID().toString();
    String ounitId = UUID.randomUUID().toString();
    String iiaId = UUID.randomUUID().toString();
    String iiaCode = UUID.randomUUID().toString();

    assertThat(repository.findByHeiIdAndIiaId(heiId, iiaId)).isEmpty();

    service.registerMapping(heiId, ounitId, iiaId, iiaCode);

    Optional<EwpInterInstitutionalAgreementMapping> mappingOptional = repository.findByHeiIdAndIiaId(
        heiId, iiaId);
    assertThat(mappingOptional).isPresent();
    assertThat(mappingOptional.get()).isNotNull();
    assertThat(mappingOptional.get().getHeiId()).isEqualTo(heiId);
    assertThat(mappingOptional.get().getOunitId()).isEqualTo(ounitId);
    assertThat(mappingOptional.get().getIiaId()).isEqualTo(iiaId);
    assertThat(mappingOptional.get().getIiaCode()).isEqualTo(iiaCode);
  }

  @Test
  void testRegisterMapping_ExistingMappingWithNewOunitId_MappingUpdated() {
    String heiId = UUID.randomUUID().toString();
    String ounitId = UUID.randomUUID().toString();
    String iiaId = UUID.randomUUID().toString();
    String iiaCode = UUID.randomUUID().toString();

    service.registerMapping(heiId, ounitId, iiaId, iiaCode);

    assertThat(repository.findByHeiIdAndIiaId(heiId, iiaId)).isPresent();

    ounitId = UUID.randomUUID().toString();
    service.registerMapping(heiId, ounitId, iiaId, iiaCode);

    Optional<EwpInterInstitutionalAgreementMapping> mappingOptional = repository.findByHeiIdAndIiaId(
        heiId, iiaId);
    assertThat(mappingOptional).isPresent();
    assertThat(mappingOptional.get()).isNotNull();
    assertThat(mappingOptional.get().getHeiId()).isEqualTo(heiId);
    assertThat(mappingOptional.get().getOunitId()).isEqualTo(ounitId);
    assertThat(mappingOptional.get().getIiaId()).isEqualTo(iiaId);
    assertThat(mappingOptional.get().getIiaCode()).isEqualTo(iiaCode);
  }
}