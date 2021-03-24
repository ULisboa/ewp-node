package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ounits;

import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import java.util.List;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import org.springdoc.api.annotations.ParameterObject;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ounits.OrganizationalUnitsRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.ounits.EwpOrganizationalUnitsV2Client;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "ounits/v2")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiOrganizationalUnitsV2Controller extends AbstractForwardEwpApiController {

  private final EwpOrganizationalUnitsV2Client client;

  public ForwardEwpApiOrganizationalUnitsV2Controller(
      RegistryClient registryClient, EwpOrganizationalUnitsV2Client client) {
    super(registryClient);
    this.client = client;
  }

  @GetMapping(value = "/specification", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Returns the specification for the API when considering a given HEI ID.",
      description =
          "The specification returned contains the maximum number of ounit IDs and codes that the target HEI ID accepts per request.",
      tags = {"Organizational Units"})
  public ResponseEntity<
          ForwardEwpApiResponseWithData<
              ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO>>
      getApiSpecification(@NotEmpty @RequestParam(value = "hei_id") String heiId) {
    ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO apiSpecification =
        client.getApiSpecification(heiId);
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(apiSpecification));
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Organizational Units Forward API.",
      tags = {"Organizational Units"})
  public ResponseEntity<ForwardEwpApiResponseWithData<OunitsResponseV2>> organizationalUnitsGet(
      @Valid @ParameterObject @RequestParam OrganizationalUnitsRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getOrganizationalUnits(
        requestDto.getHeiId(),
        requestDto.getOrganizationalUnitIds(),
        requestDto.getOrganizationalUnitCodes());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Organizational Units Forward API.",
      tags = {"Organizational Units"})
  public ResponseEntity<ForwardEwpApiResponseWithData<OunitsResponseV2>> organizationalUnitsPost(
      @Valid OrganizationalUnitsRequestDto requestDto) throws AbstractEwpClientErrorException {
    return getOrganizationalUnits(
        requestDto.getHeiId(),
        requestDto.getOrganizationalUnitIds(),
        requestDto.getOrganizationalUnitCodes());
  }

  // NOTE: currently only allows to obtain by ounit IDs or ounit codes (not both simultaneously)
  private ResponseEntity<ForwardEwpApiResponseWithData<OunitsResponseV2>> getOrganizationalUnits(
      String heiId, List<String> organizationalUnitIds, List<String> organizationalUnitCodes)
      throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<OunitsResponseV2> ounitsResponse;
    if (!organizationalUnitIds.isEmpty()) {
      ounitsResponse = client.findByOunitIds(heiId, organizationalUnitIds);
    } else {
      ounitsResponse = client.findByOunitCodes(heiId, organizationalUnitCodes);
    }
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(ounitsResponse);
  }
}
