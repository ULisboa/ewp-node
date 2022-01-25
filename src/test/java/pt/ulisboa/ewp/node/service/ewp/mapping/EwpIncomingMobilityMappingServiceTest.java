package pt.ulisboa.ewp.node.service.ewp.mapping;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpIncomingMobilityMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpIncomingMobilityMappingRepository;

class EwpIncomingMobilityMappingServiceTest extends AbstractIntegrationTest {

  @Autowired
  private EwpIncomingMobilityMappingRepository repository;

  @Autowired
  private EwpIncomingMobilityMappingService service;

  @Test
  void testRegisterMapping_NewMapping_MappingCreated() {
    String receivingHeiId = UUID.randomUUID().toString();
    String receivingOunitId = UUID.randomUUID().toString();
    String omobilityId = UUID.randomUUID().toString();

    assertThat(
        repository.findByReceivingHeiIdAndOmobilityId(receivingHeiId, omobilityId)).isEmpty();

    service.registerMapping(receivingHeiId, receivingOunitId, omobilityId);

    Optional<EwpIncomingMobilityMapping> mappingOptional = repository.findByReceivingHeiIdAndOmobilityId(
        receivingHeiId, omobilityId);
    assertThat(mappingOptional).isPresent();
    assertThat(mappingOptional.get()).isNotNull();
    assertThat(mappingOptional.get().getReceivingHeiId()).isEqualTo(receivingHeiId);
    assertThat(mappingOptional.get().getReceivingOunitId()).isEqualTo(receivingOunitId);
    assertThat(mappingOptional.get().getOmobilityId()).isEqualTo(omobilityId);
  }

  @Test
  void testRegisterMapping_ExistingMappingWithNewOunitId_MappingUpdated() {
    String receivingHeiId = UUID.randomUUID().toString();
    String receivingOunitId = UUID.randomUUID().toString();
    String omobilityId = UUID.randomUUID().toString();

    service.registerMapping(receivingHeiId, receivingOunitId, omobilityId);

    assertThat(
        repository.findByReceivingHeiIdAndOmobilityId(receivingHeiId, omobilityId)).isPresent();

    receivingOunitId = UUID.randomUUID().toString();
    service.registerMapping(receivingHeiId, receivingOunitId, omobilityId);

    Optional<EwpIncomingMobilityMapping> mappingOptional = repository.findByReceivingHeiIdAndOmobilityId(
        receivingHeiId, omobilityId);
    assertThat(mappingOptional).isPresent();
    assertThat(mappingOptional.get()).isNotNull();
    assertThat(mappingOptional.get().getReceivingHeiId()).isEqualTo(receivingHeiId);
    assertThat(mappingOptional.get().getReceivingOunitId()).isEqualTo(receivingOunitId);
    assertThat(mappingOptional.get().getOmobilityId()).isEqualTo(omobilityId);
  }
}