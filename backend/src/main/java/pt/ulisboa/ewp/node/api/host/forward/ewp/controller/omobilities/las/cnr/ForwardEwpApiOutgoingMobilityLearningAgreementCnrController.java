package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.omobilities.las.cnr;

import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.las.cnr.ForwardEwpApiOutgoingMobilityLearningAgreementCnrRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityLearningAgreementChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.ewp.mapping.EwpOutgoingMobilityMappingService;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.OUTGOING_MOBILITY_LEARNING_AGREEMENT_CNR)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "omobilities/las/cnr")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiOutgoingMobilityLearningAgreementCnrController extends
    AbstractForwardEwpApiController {

  private final EwpChangeNotificationRepository changeNotificationRepository;
  private final EwpOutgoingMobilityMappingService outgoingMobilityMappingService;

  public ForwardEwpApiOutgoingMobilityLearningAgreementCnrController(
      RegistryClient registryClient,
      EwpChangeNotificationRepository changeNotificationRepository,
      EwpOutgoingMobilityMappingService outgoingMobilityMappingService) {
    super(registryClient);
    this.changeNotificationRepository = changeNotificationRepository;
    this.outgoingMobilityMappingService = outgoingMobilityMappingService;
  }

  @ForwardEwpApiEndpoint(api = "omobility-la-cnr")
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<ForwardEwpApiResponse> sendChangeNotification(
      @Valid ForwardEwpApiOutgoingMobilityLearningAgreementCnrRequestDto requestDto) {
    CommunicationLog currentCommunicationLog =
        CommunicationContextHolder.getContext().getCurrentCommunicationLog();
    int index = 0;
    for (String outgoingMobilityId : requestDto.getOutgoingMobilityIds()) {
      EwpOutgoingMobilityLearningAgreementChangeNotification changeNotification =
          new EwpOutgoingMobilityLearningAgreementChangeNotification(
              currentCommunicationLog,
              requestDto.getSendingHeiId(),
              requestDto.getReceivingHeiId(),
              outgoingMobilityId);
      changeNotificationRepository.persist(changeNotification);

      outgoingMobilityMappingService.registerMapping(requestDto.getSendingHeiId(),
          requestDto.getSendingOunitId(), requestDto.getOutgoingMobilityIds().get(index));

      index++;
    }
    return ForwardEwpApiResponseUtils.toAcceptedResponseEntity();
  }
}
