package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.omobilities.las.cnr;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.AbstractForwardEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpOutgoingMobilityMappingRepository;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

class ForwardEwpApiOutgoingMobilityLearningAgreementCnrControllerTest extends
    AbstractForwardEwpControllerIntegrationTest {

  @Autowired
  private EwpOutgoingMobilityMappingRepository mappingRepository;

  @Test
  void testSendChangeNotification_ValidRequestWithTwoOmobilityIds_TwoMappingsPersisted()
      throws Exception {
    String sendingHeiId = UUID.randomUUID().toString();
    String sendingOunitId = UUID.randomUUID().toString();
    String receivingHeiId = UUID.randomUUID().toString();
    List<String> outgoingMobilityIds = Arrays.asList("a1", "b2");

    for (String omobilityId : outgoingMobilityIds) {
      assertThat(mappingRepository.findByHeiIdAndOmobilityId(sendingHeiId, omobilityId)).isEmpty();
    }

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    bodyParams.param(EwpApiParamConstants.SENDING_OUNIT_ID, sendingOunitId);
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    executeRequest(HttpMethod.POST, ForwardEwpApiConstants.API_BASE_URI + "omobilities/las/cnr",
        bodyParams);

    for (String omobilityId : outgoingMobilityIds) {
      Optional<EwpOutgoingMobilityMapping> firstMappingOptional = mappingRepository.findByHeiIdAndOmobilityId(
          sendingHeiId, omobilityId);
      assertThat(firstMappingOptional).isPresent();
      EwpOutgoingMobilityMapping firstMapping = firstMappingOptional.get();
      assertThat(firstMapping.getHeiId()).isEqualTo(sendingHeiId);
      assertThat(firstMapping.getOunitId()).isEqualTo(sendingOunitId);
      assertThat(firstMapping.getOmobilityId()).isEqualTo(omobilityId);
    }
  }

}