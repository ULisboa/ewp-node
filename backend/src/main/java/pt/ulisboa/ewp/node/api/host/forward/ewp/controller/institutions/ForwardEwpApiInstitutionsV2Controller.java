package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.institutions;

import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springdoc.api.annotations.ParameterObject;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.institutions.InstitutionsRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.institutions.EwpInstitutionsV2Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.INSTITUTIONS)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "institutions/v2")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiInstitutionsV2Controller extends AbstractForwardEwpApiController {

  private final EwpInstitutionsV2Client client;

  public ForwardEwpApiInstitutionsV2Controller(
      RegistryClient registryClient, EwpInstitutionsV2Client client) {
    super(registryClient);
    this.client = client;
  }

  @ForwardEwpApiEndpoint(
      api = "institutions",
      apiMajorVersion = 2,
      targetHeiIdParameterName = EwpApiParamConstants.HEI_ID)
  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Institutions"})
  public ResponseEntity<ForwardEwpApiResponseWithData<InstitutionsResponseV2>> institutionsGet(
      @Valid @ParameterObject @RequestParam InstitutionsRequestDto requestDto)
      throws EwpClientErrorException {
    return getInstitution(requestDto);
  }

  @ForwardEwpApiEndpoint(
      api = "institutions",
      apiMajorVersion = 2,
      targetHeiIdParameterName = EwpApiParamConstants.HEI_ID)
  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Institutions"})
  public ResponseEntity<ForwardEwpApiResponseWithData<InstitutionsResponseV2>> institutionsPost(
      @Valid InstitutionsRequestDto requestDto) throws EwpClientErrorException {
    return getInstitution(requestDto);
  }

  // NOTE: currently only allows one HEI ID each time
  private ResponseEntity<ForwardEwpApiResponseWithData<InstitutionsResponseV2>> getInstitution(
      InstitutionsRequestDto requestDto) throws EwpClientErrorException {
    EwpSuccessOperationResult<InstitutionsResponseV2> institutionsResponse =
        client.find(requestDto.getHeiId());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(institutionsResponse);
  }
}
