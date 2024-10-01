package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.omobilities.cnr;

import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.cnr.ForwardEwpApiCnrSubmissionResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.cnr.ForwardEwpApiOutgoingMobilityCnrRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.ewp.mapping.EwpOutgoingMobilityMappingService;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.OUTGOING_MOBILITY_CNR)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "omobilities/cnr")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiOutgoingMobilityCnrController extends AbstractForwardEwpApiController {

  private final EwpChangeNotificationRepository changeNotificationRepository;
  private final EwpOutgoingMobilityMappingService outgoingMobilityMappingService;

  public ForwardEwpApiOutgoingMobilityCnrController(
      RegistryClient registryClient,
      EwpChangeNotificationRepository changeNotificationRepository,
      EwpOutgoingMobilityMappingService outgoingMobilityMappingService) {
    super(registryClient);
    this.changeNotificationRepository = changeNotificationRepository;
    this.outgoingMobilityMappingService = outgoingMobilityMappingService;
  }

  @ForwardEwpApiEndpoint(
      api = "omobility-cnr",
      targetHeiIdParameterName = EwpApiParamConstants.RECEIVING_HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiCnrSubmissionResponseDTO>>
      sendChangeNotification(@Valid ForwardEwpApiOutgoingMobilityCnrRequestDto requestDto) {
    CommunicationLog currentCommunicationLog =
        CommunicationContextHolder.getContext().getCurrentCommunicationLog();
    int index = 0;
    Collection<Long> changeNotificationIds = new ArrayList<>();
    for (String outgoingMobilityId : requestDto.getOutgoingMobilityIds()) {
      EwpOutgoingMobilityChangeNotification changeNotification =
          new EwpOutgoingMobilityChangeNotification(
              currentCommunicationLog,
              requestDto.getSendingHeiId(),
              requestDto.getReceivingHeiId(),
              outgoingMobilityId);
      changeNotificationRepository.persist(changeNotification);

      outgoingMobilityMappingService.registerMapping(requestDto.getSendingHeiId(),
          requestDto.getSendingOunitId(), requestDto.getOutgoingMobilityIds().get(index));

      changeNotificationIds.add(changeNotification.getId());

      index++;
    }
    return ForwardEwpApiResponseUtils.toAcceptedResponseEntity(
        new ForwardEwpApiCnrSubmissionResponseDTO(changeNotificationIds));
  }
}
