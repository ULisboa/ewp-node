package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.iias.approval;

import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalResponseV1;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval.ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval.InterInstitutionalAgreementsApprovalGetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.iias.approval.EwpInterInstitutionalAgreementsApprovalsV1Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@RestController
@ForwardEwpApi(apiLocalName = EwpApiConstants.API_INTERINSTITUTIONAL_AGREEMENTS_APPROVAL_NAME)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "iias/approval/v1")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiInterInstitutionalAgreementsApprovalV1Controller
    extends AbstractForwardEwpApiController {

  private final EwpInterInstitutionalAgreementsApprovalsV1Client client;

  public ForwardEwpApiInterInstitutionalAgreementsApprovalV1Controller(
      RegistryClient registryClient, EwpInterInstitutionalAgreementsApprovalsV1Client client) {
    super(registryClient);
    this.client = client;
  }

  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
      ForwardEwpApiResponseWithData<
          ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO>>
  getApiSpecification(@NotEmpty @RequestParam(value = "hei_id") String heiId) {
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            client.getApiSpecification(heiId)));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasApprovalResponseV1>> getApprovals(
      @Valid InterInstitutionalAgreementsApprovalGetRequestDto requestDto)
      throws EwpClientErrorException {
    EwpSuccessOperationResult<IiasApprovalResponseV1> response =
        client.getApprovals(
            requestDto.getApprovingHeiId(),
            requestDto.getOwnerHeiId(),
            requestDto.getIiaIds(),
            requestDto.getSendPdf());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
