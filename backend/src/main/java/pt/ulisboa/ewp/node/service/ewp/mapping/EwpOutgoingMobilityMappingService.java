package pt.ulisboa.ewp.node.service.ewp.mapping;

import java.util.Optional;
import javax.transaction.Transactional;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpOutgoingMobilityMappingRepository;

@Service
@Transactional
public class EwpOutgoingMobilityMappingService {

  private final EwpOutgoingMobilityMappingRepository repository;

  public EwpOutgoingMobilityMappingService(
      EwpOutgoingMobilityMappingRepository repository) {
    this.repository = repository;
  }

  public Optional<EwpOutgoingMobilityMapping> getMapping(String heiId, String outgoingMobilityId) {
    return repository.findByHeiIdAndOmobilityId(heiId, outgoingMobilityId);
  }

  @Transactional
  public void registerMapping(String heiId, String organizationalUnitId,
      String outgoingMobilityId) {
    Optional<EwpOutgoingMobilityMapping> mappingOptional = repository.findByHeiIdAndOmobilityId(
        heiId, outgoingMobilityId);

    EwpOutgoingMobilityMapping mapping;
    if (mappingOptional.isEmpty()) {
      mapping = EwpOutgoingMobilityMapping.create(
          heiId, organizationalUnitId, outgoingMobilityId);

    } else {
      mapping = mappingOptional.get();
      mapping.setOunitId(organizationalUnitId);
    }

    if (!repository.persist(mapping)) {
      throw new IllegalStateException();
    }
  }
}
