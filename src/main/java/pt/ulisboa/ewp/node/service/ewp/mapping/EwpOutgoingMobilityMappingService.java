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

  @Transactional
  public void registerMapping(String heiId, String ounitId, String omobilityId) {
    Optional<EwpOutgoingMobilityMapping> mappingOptional = repository.findByHeiIdAndOmobilityId(
        heiId, omobilityId);

    EwpOutgoingMobilityMapping mapping;
    if (mappingOptional.isEmpty()) {
      mapping = EwpOutgoingMobilityMapping.create(
          heiId, ounitId, omobilityId);

    } else {
      mapping = mappingOptional.get();
      mapping.setOunitId(ounitId);
    }

    if (!repository.persist(mapping)) {
      throw new IllegalStateException();
    }
  }
}
