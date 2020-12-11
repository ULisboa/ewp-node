package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.iias;

import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasGetResponseV4;
import eu.erasmuswithoutpaper.api.iias.v4.endpoints.IiasIndexResponseV4;
import java.util.List;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.iias.EwpInterInstitutionalAgreementsV4Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "iias/v4")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiInterInstitutionalAgreementsV4Controller
    extends AbstractForwardEwpApiInterInstitutionalAgreementsController {

  private final EwpInterInstitutionalAgreementsV4Client client;

  public ForwardEwpApiInterInstitutionalAgreementsV4Controller(
      EwpInterInstitutionalAgreementsV4Client client) {
    this.client = client;
  }

  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
          ForwardEwpApiResponseWithData<
              ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(value = "hei_id") String heiId) {
    ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO apiSpecification =
        client.getApiSpecification(heiId);
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(apiSpecification));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasIndexResponseV4>> findAllByHeiId(
      @Valid InterInstitutionalAgreementsIndexRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<IiasIndexResponseV4> response =
        client.findAllByHeiIds(
            requestDto.getHeiId(),
            requestDto.getPartnerHeiId(),
            requestDto.getReceivingAcademicYearIds(),
            requestDto.getModifiedSince());
    return createResponseEntityFromOperationResult(response);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<IiasGetResponseV4>> findByHeiIdAndIiaIds(
      @Valid InterInstitutionalAgreementsGetRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<IiasGetResponseV4> response;
    List<String> iiaIds = requestDto.getIiaIds();
    List<String> iiaCodes = requestDto.getIiaCodes();
    if (!iiaIds.isEmpty()) {
      response =
          client.findByHeiIdAndIiaIds(requestDto.getHeiId(), iiaIds, requestDto.getSendPdf());
    } else {
      response =
          client.findByHeiIdAndIiaCodes(requestDto.getHeiId(), iiaCodes, requestDto.getSendPdf());
    }
    return createResponseEntityFromOperationResult(response);
  }
}
