package pt.ulisboa.ewp.node.service.ewp.mapping.cache;

import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.StudentMobilityForStudiesV1;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.service.ewp.mapping.EwpIncomingMobilityMappingService;

/**
 * Service that caches mobility mappings from actual EWP responses.
 */
@Service
public class EwpMobilityMappingCacheService {

  private final EwpIncomingMobilityMappingService incomingMobilityMappingService;

  public EwpMobilityMappingCacheService(
      EwpIncomingMobilityMappingService incomingMobilityMappingService) {
    this.incomingMobilityMappingService = incomingMobilityMappingService;
  }

  public void cacheMappingsFrom(OmobilitiesGetResponseV1 omobilitiesGetResponseV1) {
    for (StudentMobilityForStudiesV1 mobility : omobilitiesGetResponseV1.getSingleMobilityObject()) {
      this.incomingMobilityMappingService.registerMapping(mobility.getReceivingHei().getHeiId(),
          mobility.getReceivingHei().getOunitId(), mobility.getOmobilityId());
    }
  }

}
