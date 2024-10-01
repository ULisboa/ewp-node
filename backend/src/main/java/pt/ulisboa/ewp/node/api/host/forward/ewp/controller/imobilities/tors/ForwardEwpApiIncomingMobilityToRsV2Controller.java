package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.imobilities.tors;

import eu.erasmuswithoutpaper.api.imobilities.tors.v2.endpoints.ImobilityTorsGetResponseV2;
import eu.erasmuswithoutpaper.api.imobilities.tors.v2.endpoints.ImobilityTorsIndexResponseV2;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.tors.ForwardEwpApiIncomingMobilityToRsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.tors.ForwardEwpApiIncomingMobilityToRsGetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.tors.ForwardEwpApiIncomingMobilityToRsIndexRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.imobilities.tors.EwpIncomingMobilityToRsV2Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.INCOMING_MOBILITY_TORS)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "imobilities/tors/v2")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiIncomingMobilityToRsV2Controller extends AbstractForwardEwpApiController {

  private final EwpIncomingMobilityToRsV2Client client;

  public ForwardEwpApiIncomingMobilityToRsV2Controller(
      RegistryClient registryClient, EwpIncomingMobilityToRsV2Client client) {
    super(registryClient);
    this.client = client;
  }

  @ForwardEwpApiEndpoint(
      api = "imobility-tors",
      apiMajorVersion = 2,
      endpoint = "specification",
      targetHeiIdParameterName = EwpApiParamConstants.HEI_ID)
  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
          ForwardEwpApiResponseWithData<
              ForwardEwpApiIncomingMobilityToRsApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(EwpApiParamConstants.HEI_ID) String heiId) {
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            client.getApiSpecification(heiId)));
  }

  @ForwardEwpApiEndpoint(
      api = "imobility-tors",
      apiMajorVersion = 2,
      endpoint = "index",
      targetHeiIdParameterName = EwpApiParamConstants.RECEIVING_HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<ImobilityTorsIndexResponseV2>>
      findOutgoingMobilityIdsWithTranscriptOfRecord(
          @Valid ForwardEwpApiIncomingMobilityToRsIndexRequestDto requestDto)
          throws EwpClientErrorException {
    EwpSuccessOperationResult<ImobilityTorsIndexResponseV2> response =
        client.findOutgoingMobilityIdsWithTranscriptOfRecord(
            requestDto.getReceivingHeiId(), requestDto.getSendingHeiIds(),
            requestDto.getModifiedSince());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @ForwardEwpApiEndpoint(
      api = "imobility-tors",
      apiMajorVersion = 2,
      endpoint = "get",
      targetHeiIdParameterName = EwpApiParamConstants.RECEIVING_HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<ImobilityTorsGetResponseV2>>
      findByReceivingHeiIdAndOutgoingMobilityIds(
          @Valid ForwardEwpApiIncomingMobilityToRsGetRequestDto requestDto)
          throws EwpClientErrorException {
    if (requestDto.getOmobilityIds().isEmpty()) {
      return ForwardEwpApiResponseUtils.toSuccessResponseEntity(new ImobilityTorsGetResponseV2());
    }

    EwpSuccessOperationResult<ImobilityTorsGetResponseV2> response =
        client.findByReceivingHeiIdAndOutgoingMobilityIds(
            requestDto.getReceivingHeiId(), requestDto.getOmobilityIds());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
