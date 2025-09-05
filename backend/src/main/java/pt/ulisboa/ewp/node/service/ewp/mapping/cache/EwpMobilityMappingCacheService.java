package pt.ulisboa.ewp.node.service.ewp.mapping.cache;

import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.LearningAgreementV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.StudentMobilityForStudiesV1;
import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.OmobilitiesGetResponseV2;
import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.StudentMobilityV2;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.NominationTypeV3.ReceivingHei;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesGetResponseV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.StudentMobilityV3;
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
      if (mobility.getReceivingHei() != null) {
        this.incomingMobilityMappingService.registerMapping(mobility.getReceivingHei().getHeiId(),
                mobility.getReceivingHei().getOunitId(), mobility.getOmobilityId());
      }
    }
  }

  public void cacheMappingsFrom(OmobilitiesGetResponseV2 omobilitiesGetResponseV2) {
    for (StudentMobilityV2 mobility : omobilitiesGetResponseV2.getSingleMobilityObject()) {
      if (mobility.getReceivingHei() != null) {
        this.incomingMobilityMappingService.registerMapping(mobility.getReceivingHei().getHeiId(),
                mobility.getReceivingHei().getOunitId(), mobility.getOmobilityId());
      }
    }
  }

  public void cacheMappingsFrom(OmobilitiesGetResponseV3 omobilitiesGetResponseV3) {
    for (StudentMobilityV3 mobility : omobilitiesGetResponseV3.getSingleMobilityObject()) {
      if (mobility.getNomination() != null && mobility.getNomination().getReceivingHei() != null) {
        ReceivingHei receivingHeiId = mobility.getNomination().getReceivingHei();
        if (receivingHeiId != null) {
          this.incomingMobilityMappingService.registerMapping(
              receivingHeiId.getHeiId(), receivingHeiId.getOunitId(), mobility.getOmobilityId());
        }
      }
    }
  }

  public void cacheMappingsFrom(OmobilityLasGetResponseV1 response) {
    for (LearningAgreementV1 learningAgreement : response.getLa()) {
      if (learningAgreement.getReceivingHei() != null) {
        this.incomingMobilityMappingService.registerMapping(learningAgreement.getReceivingHei().getHeiId(),
                learningAgreement.getReceivingHei().getOunitId(), learningAgreement.getOmobilityId());
      }
    }
  }

}
