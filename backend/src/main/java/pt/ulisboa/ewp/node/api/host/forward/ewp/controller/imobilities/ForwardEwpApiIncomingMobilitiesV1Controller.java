package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.imobilities;

import eu.erasmuswithoutpaper.api.imobilities.v1.endpoints.ImobilitiesGetResponseV1;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.ForwardEwpApiIncomingMobilitiesGetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.imobilities.EwpIncomingMobilitiesV1Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.INCOMING_MOBILITIES)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "imobilities/v1")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiIncomingMobilitiesV1Controller extends AbstractForwardEwpApiController {

  private final EwpIncomingMobilitiesV1Client client;

  public ForwardEwpApiIncomingMobilitiesV1Controller(
      RegistryClient registryClient, EwpIncomingMobilitiesV1Client client) {
    super(registryClient);
    this.client = client;
  }

  @ForwardEwpApiEndpoint(api = "imobilities", apiMajorVersion = 1, endpoint = "specification")
  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  public ResponseEntity<
          ForwardEwpApiResponseWithData<ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(value = "hei_id") String heiId) {
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            client.getApiSpecification(heiId)));
  }

  @ForwardEwpApiEndpoint(api = "imobilities", apiMajorVersion = 1, endpoint = "get")
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE,
      value = "/get")
  public ResponseEntity<ForwardEwpApiResponseWithData<ImobilitiesGetResponseV1>>
      findByReceivingHeiIdAndOmobilityIds(
          @Valid ForwardEwpApiIncomingMobilitiesGetRequestDto requestDto)
          throws EwpClientErrorException {
    if (requestDto.getOmobilityIds().isEmpty()) {
      return ForwardEwpApiResponseUtils.toSuccessResponseEntity(new ImobilitiesGetResponseV1());
    }

    EwpSuccessOperationResult<ImobilitiesGetResponseV1> response =
        client.findByReceivingHeiIdAndOmobilityIds(
            requestDto.getReceivingHeiId(), requestDto.getOmobilityIds());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(response);
  }
}
