package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.omobilities;

import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesIndexResponseV1;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.ForwardEwpApiOutgoingMobilitiesGetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.ForwardEwpApiOutgoingMobilitiesIndexRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.omobilities.EwpOutgoingMobilitiesV1Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "omobilities/v1")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiOutgoingMobilitiesV1Controller extends AbstractForwardEwpApiController {

  private final EwpOutgoingMobilitiesV1Client client;

  public ForwardEwpApiOutgoingMobilitiesV1Controller(
      RegistryClient registryClient, EwpOutgoingMobilitiesV1Client client) {
    super(registryClient);
    this.client = client;
  }

  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
          ForwardEwpApiResponseWithData<ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(value = "hei_id") String heiId) {
    ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO apiSpecification =
        client.getApiSpecification(heiId);
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(apiSpecification));
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/index")
  public ResponseEntity<ForwardEwpApiResponseWithData<OmobilitiesIndexResponseV1>>
      findAllBySendingHeiId(@Valid ForwardEwpApiOutgoingMobilitiesIndexRequestDto requestDto)
          throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<OmobilitiesIndexResponseV1> response =
        client.findAllBySendingHeiId(
            requestDto.getSendingHeiId(),
            requestDto.getReceivingHeiIds(),
            requestDto.getReceivingAcademicYearId(),
            requestDto.getModifiedSince());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<OmobilitiesGetResponseV1>>
      findBySendingHeiIdAndOmobilityIds(
          @Valid ForwardEwpApiOutgoingMobilitiesGetRequestDto requestDto)
          throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<OmobilitiesGetResponseV1> response =
        client.findBySendingHeiIdAndOmobilityIds(
            requestDto.getSendingHeiId(), requestDto.getOmobilityIds());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
