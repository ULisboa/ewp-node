package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.iias.approval;

import eu.erasmuswithoutpaper.api.iias.approval.v2.IiasApprovalResponseV2;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval.ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval.InterInstitutionalAgreementsApprovalGetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.iias.approval.EwpInterInstitutionalAgreementsApprovalsV2Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.INTERINSTITUTIONAL_AGREEMENTS_APPROVAL)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "iias/approval/v2")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiInterInstitutionalAgreementsApprovalV2Controller
    extends AbstractForwardEwpApiController {

  private final EwpInterInstitutionalAgreementsApprovalsV2Client client;

  public ForwardEwpApiInterInstitutionalAgreementsApprovalV2Controller(
      RegistryClient registryClient, EwpInterInstitutionalAgreementsApprovalsV2Client client) {
    super(registryClient);
    this.client = client;
  }

  @ForwardEwpApiEndpoint(
      api = "iias-approval",
      apiMajorVersion = 2,
      endpoint = "specification",
      targetHeiIdParameterName = EwpApiParamConstants.HEI_ID)
  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
          ForwardEwpApiResponseWithData<
              ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(EwpApiParamConstants.HEI_ID) String heiId) {
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            client.getApiSpecification(heiId)));
  }

  @ForwardEwpApiEndpoint(
      api = "iias-approval",
      apiMajorVersion = 2,
      targetHeiIdParameterName = EwpApiParamConstants.APPROVING_HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasApprovalResponseV2>> getApprovals(
      @Valid InterInstitutionalAgreementsApprovalGetRequestDto requestDto)
      throws EwpClientErrorException {
    EwpSuccessOperationResult<IiasApprovalResponseV2> response =
        client.getApprovals(requestDto.getApprovingHeiId(), requestDto.getIiaIds());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
