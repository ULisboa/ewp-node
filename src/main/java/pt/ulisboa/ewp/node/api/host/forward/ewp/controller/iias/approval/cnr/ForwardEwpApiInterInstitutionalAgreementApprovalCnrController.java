package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.iias.approval.cnr;

import javax.validation.Valid;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval.cnr.ForwardEwpApiInterInstitutionalAgreementApprovalCnrRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpInterInstitutionalAgreementApprovalChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.INTERINSTITUTIONAL_AGREEMENTS_APPROVAL_CNR)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "iias/approval/cnr")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiInterInstitutionalAgreementApprovalCnrController extends
    AbstractForwardEwpApiController {

  private final EwpChangeNotificationRepository changeNotificationRepository;

  public ForwardEwpApiInterInstitutionalAgreementApprovalCnrController(
      RegistryClient registryClient,
      EwpChangeNotificationRepository changeNotificationRepository) {
    super(registryClient);
    this.changeNotificationRepository = changeNotificationRepository;
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<ForwardEwpApiResponse>
  sendChangeNotification(
      @Valid ForwardEwpApiInterInstitutionalAgreementApprovalCnrRequestDto requestDto) {
    EwpInterInstitutionalAgreementApprovalChangeNotification changeNotification = new EwpInterInstitutionalAgreementApprovalChangeNotification(
        requestDto.getApprovingHeiId(),
        requestDto.getPartnerHeiId(), requestDto.getOwnerId(), requestDto.getIiaId());
    changeNotificationRepository.persist(changeNotification);
    return ForwardEwpApiResponseUtils.toAcceptedResponseEntity();
  }
}
