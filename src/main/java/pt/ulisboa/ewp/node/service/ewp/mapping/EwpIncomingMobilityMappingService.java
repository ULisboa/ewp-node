package pt.ulisboa.ewp.node.service.ewp.mapping;

import java.util.Collection;
import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpIncomingMobilityMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpIncomingMobilityMappingRepository;

@Service
@Transactional
public class EwpIncomingMobilityMappingService {

  private final EwpIncomingMobilityMappingRepository repository;

  public EwpIncomingMobilityMappingService(
      EwpIncomingMobilityMappingRepository repository) {
    this.repository = repository;
  }

  public Collection<EwpIncomingMobilityMapping> getAllMappings() {
    return repository.findAll();
  }

  public Optional<EwpIncomingMobilityMapping> getMapping(String receivingHeiId,
      String outgoingMobilityId) {
    return repository.findByReceivingHeiIdAndOmobilityId(receivingHeiId, outgoingMobilityId);
  }

  @Transactional
  public void registerMapping(String receivingHeiId, String receivingOunitId,
      String outgoingMobilityId) {
    Optional<EwpIncomingMobilityMapping> mappingOptional = repository.findByReceivingHeiIdAndOmobilityId(
        receivingHeiId, outgoingMobilityId);

    EwpIncomingMobilityMapping mapping;
    if (mappingOptional.isEmpty()) {
      mapping = EwpIncomingMobilityMapping.create(
          receivingHeiId, receivingOunitId, outgoingMobilityId);

    } else {
      mapping = mappingOptional.get();
      mapping.setReceivingOunitId(receivingOunitId);
    }

    if (!repository.persist(mapping)) {
      throw new IllegalStateException();
    }
  }
}
