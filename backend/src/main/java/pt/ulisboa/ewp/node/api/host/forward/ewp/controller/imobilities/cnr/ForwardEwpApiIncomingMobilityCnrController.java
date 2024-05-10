package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.imobilities.cnr;

import javax.validation.Valid;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.cnr.ForwardEwpApiIncomingMobilityCnrRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.communication.log.CommunicationLog;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpIncomingMobilityChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.INCOMING_MOBILITY_CNR)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "imobilities/cnr")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiIncomingMobilityCnrController extends AbstractForwardEwpApiController {

  private final EwpChangeNotificationRepository changeNotificationRepository;

  public ForwardEwpApiIncomingMobilityCnrController(
      RegistryClient registryClient,
      EwpChangeNotificationRepository changeNotificationRepository) {
    super(registryClient);
    this.changeNotificationRepository = changeNotificationRepository;
  }

  @ForwardEwpApiEndpoint(
      api = "imobility-cnr",
      targetHeiIdParameterName = EwpApiParamConstants.SENDING_HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<ForwardEwpApiResponse> sendChangeNotification(
      @Valid ForwardEwpApiIncomingMobilityCnrRequestDto requestDto) {
    CommunicationLog currentCommunicationLog =
        CommunicationContextHolder.getContext().getCurrentCommunicationLog();
    for (String outgoingMobilityId : requestDto.getOutgoingMobilityIds()) {
      EwpIncomingMobilityChangeNotification changeNotification =
          new EwpIncomingMobilityChangeNotification(
              currentCommunicationLog,
              requestDto.getSendingHeiId(),
              requestDto.getReceivingHeiId(),
              outgoingMobilityId);
      changeNotificationRepository.persist(changeNotification);
    }
    return ForwardEwpApiResponseUtils.toAcceptedResponseEntity();
  }
}
