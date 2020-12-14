package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.institutions;

import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import javax.validation.Valid;
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
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiHeiIdsResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.institutions.InstitutionsRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.EwpInstitutionsClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "institutions/v2")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiInstitutionsV2Controller extends AbstractForwardEwpApiController {

  private final EwpInstitutionsClient client;

  public ForwardEwpApiInstitutionsV2Controller(
      RegistryClient registryClient, EwpInstitutionsClient client) {
    super(registryClient);
    this.client = client;
  }

  @GetMapping(value = "/hei-ids", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Institutions"})
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiHeiIdsResponseDTO>>
      getAllHeiIds() {
    Collection<String> heiIds = getRegistryClient().getAllHeiIds();
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            new ForwardEwpApiHeiIdsResponseDTO(heiIds)));
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Institutions"})
  public ResponseEntity<ForwardEwpApiResponseWithData<InstitutionsResponseV2>> institutionsGet(
      @Valid @ParameterObject @RequestParam InstitutionsRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getInstitution(requestDto.getHeiId());
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE,
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Institutions"})
  public ResponseEntity<ForwardEwpApiResponseWithData<InstitutionsResponseV2>> institutionsPost(
      @Valid InstitutionsRequestDto requestDto) throws AbstractEwpClientErrorException {
    return getInstitution(requestDto.getHeiId());
  }

  // NOTE: currently only allows one HEI ID each time
  private ResponseEntity<ForwardEwpApiResponseWithData<InstitutionsResponseV2>> getInstitution(
      String heiId) throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<InstitutionsResponseV2> institutionsResponse = client.find(heiId);
    return createResponseEntityFromOperationResult(institutionsResponse);
  }

  @Override
  public String getApiLocalName() {
    throw new UnsupportedOperationException();
  }
}
