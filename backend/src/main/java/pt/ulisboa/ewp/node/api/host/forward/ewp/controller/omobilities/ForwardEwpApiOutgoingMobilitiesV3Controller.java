package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.omobilities;

import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesGetResponseV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesIndexResponseV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesUpdateRequestV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesUpdateResponseV3;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.ForwardEwpApiOutgoingMobilitiesGetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.ForwardEwpApiOutgoingMobilitiesIndexRequestV3Dto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.omobilities.EwpOutgoingMobilitiesV3Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.OUTGOING_MOBILITIES)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "omobilities/v3")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiOutgoingMobilitiesV3Controller extends AbstractForwardEwpApiController {

  private final EwpOutgoingMobilitiesV3Client client;

  public ForwardEwpApiOutgoingMobilitiesV3Controller(
      RegistryClient registryClient, EwpOutgoingMobilitiesV3Client client) {
    super(registryClient);
    this.client = client;
  }

  @ForwardEwpApiEndpoint(
      api = "omobilities",
      apiMajorVersion = 3,
      endpoint = "specification",
      targetHeiIdParameterName = EwpApiParamConstants.HEI_ID)
  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
          ForwardEwpApiResponseWithData<ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(EwpApiParamConstants.HEI_ID) String heiId) {
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            client.getApiSpecification(heiId)));
  }

  @ForwardEwpApiEndpoint(
      api = "omobilities",
      apiMajorVersion = 3,
      endpoint = "index",
      targetHeiIdParameterName = EwpApiParamConstants.SENDING_HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<OmobilitiesIndexResponseV3>>
      findAllBySendingHeiId(@Valid ForwardEwpApiOutgoingMobilitiesIndexRequestV3Dto requestDto)
          throws EwpClientErrorException {
    EwpSuccessOperationResult<OmobilitiesIndexResponseV3> response =
        client.findAllBySendingHeiId(
            requestDto.getSendingHeiId(),
            requestDto.getReceivingHeiId(),
            requestDto.getReceivingAcademicYearId(),
            requestDto.getModifiedSince(),
            requestDto.getGlobalId(),
            requestDto.getActivityAttributes());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @ForwardEwpApiEndpoint(
      api = "omobilities",
      apiMajorVersion = 3,
      endpoint = "get",
      targetHeiIdParameterName = EwpApiParamConstants.SENDING_HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<OmobilitiesGetResponseV3>>
      findBySendingHeiIdAndOmobilityIds(
          @Valid ForwardEwpApiOutgoingMobilitiesGetRequestDto requestDto)
          throws EwpClientErrorException {
    if (requestDto.getOmobilityIds().isEmpty()) {
      return ForwardEwpApiResponseUtils.toSuccessResponseEntity(new OmobilitiesGetResponseV3());
    }

    EwpSuccessOperationResult<OmobilitiesGetResponseV3> response =
        client.findBySendingHeiIdAndOmobilityIds(
            requestDto.getSendingHeiId(), requestDto.getOmobilityIds());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @ForwardEwpApiEndpoint(
      api = "omobilities",
      apiMajorVersion = 3,
      endpoint = "update",
      targetHeiIdParameterName = EwpApiParamConstants.SENDING_HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_XML_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/update")
  public ResponseEntity<ForwardEwpApiResponseWithData<OmobilitiesUpdateResponseV3>> updateOmobility(
      @RequestParam(EwpApiParamConstants.SENDING_HEI_ID) String sendingHeiId,
      @Valid @RequestBody OmobilitiesUpdateRequestV3 updateData)
      throws EwpClientErrorException {
    EwpSuccessOperationResult<OmobilitiesUpdateResponseV3> response =
        client.updateOmobility(sendingHeiId, updateData);

    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
