package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.omobilities.las;

import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasIndexResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateRequestV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateResponseV1;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.las.ForwardEwpApiOutgoingMobilityLearningAgreementsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.las.ForwardEwpApiOutgoingMobilityLearningAgreementsGetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.las.ForwardEwpApiOutgoingMobilityLearningAgreementsIndexRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.omobilities.las.EwpOutgoingMobilityLearningAgreementsV1Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.OUTGOING_MOBILITY_LEARNING_AGREEMENTS)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "omobilities/las/v1")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiOutgoingMobilityLearningAgreementsV1Controller extends
    AbstractForwardEwpApiController {

  private final EwpOutgoingMobilityLearningAgreementsV1Client client;

  public ForwardEwpApiOutgoingMobilityLearningAgreementsV1Controller(
      RegistryClient registryClient, EwpOutgoingMobilityLearningAgreementsV1Client client) {
    super(registryClient);
    this.client = client;
  }

  @ForwardEwpApiEndpoint(api = "omobility-las", apiMajorVersion = 1, endpoint = "specification")
  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
          ForwardEwpApiResponseWithData<
              ForwardEwpApiOutgoingMobilityLearningAgreementsApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(value = "hei_id") String heiId) {
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils
            .createResponseWithMessagesAndData(client.getApiSpecification(heiId)));
  }

  @ForwardEwpApiEndpoint(api = "omobility-las", apiMajorVersion = 1, endpoint = "index")
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<OmobilityLasIndexResponseV1>>
      findOutgoingMobilityIdsWithLearningAgreement(
          @Valid ForwardEwpApiOutgoingMobilityLearningAgreementsIndexRequestDto requestDto)
          throws EwpClientErrorException {
    EwpSuccessOperationResult<OmobilityLasIndexResponseV1> response =
        client.findOutgoingMobilityIdsWithLearningAgreement(
            requestDto.getSendingHeiId(),
            requestDto.getReceivingHeiIds(),
            requestDto.getReceivingAcademicYearId(),
            requestDto.getGlobalId(),
            requestDto.getMobilityType(),
            requestDto.getModifiedSince());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @ForwardEwpApiEndpoint(api = "omobility-las", apiMajorVersion = 1, endpoint = "get")
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<OmobilityLasGetResponseV1>>
      findBySendingHeiIdAndOutgoingMobilityIds(
          @Valid ForwardEwpApiOutgoingMobilityLearningAgreementsGetRequestDto requestDto)
          throws EwpClientErrorException {
    if (requestDto.getOmobilityIds().isEmpty()) {
      return ForwardEwpApiResponseUtils.toSuccessResponseEntity(new OmobilityLasGetResponseV1());
    }

    EwpSuccessOperationResult<OmobilityLasGetResponseV1> response =
        client.findBySendingHeiIdAndOutgoingMobilityIds(
            requestDto.getSendingHeiId(), requestDto.getOmobilityIds());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @ForwardEwpApiEndpoint(api = "omobility-las", apiMajorVersion = 1, endpoint = "update")
  @PostMapping(
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/update")
  public ResponseEntity<ForwardEwpApiResponseWithData<OmobilityLasUpdateResponseV1>>
      updateOutgoingMobilityLearningAgreement(
          @Valid @RequestBody OmobilityLasUpdateRequestV1 updateData)
          throws EwpClientErrorException {
    EwpSuccessOperationResult<OmobilityLasUpdateResponseV1> response =
        client.updateOutgoingMobilityLearningAgreement(updateData);
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
