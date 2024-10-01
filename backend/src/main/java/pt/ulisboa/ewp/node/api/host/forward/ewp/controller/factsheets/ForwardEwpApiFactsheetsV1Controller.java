package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.factsheets;

import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.ForwardEwpApiEndpoint;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.factsheet.FactsheetRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.factsheet.EwpFactsheetsV1Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.FACTSHEETS)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "factsheets/v1")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
public class ForwardEwpApiFactsheetsV1Controller extends AbstractForwardEwpApiController {

  private final EwpFactsheetsV1Client client;

  public ForwardEwpApiFactsheetsV1Controller(
      RegistryClient registryClient, EwpFactsheetsV1Client client) {
    super(registryClient);
    this.client = client;
  }

  @ForwardEwpApiEndpoint(
      api = "factsheet",
      apiMajorVersion = 1,
      targetHeiIdParameterName = EwpApiParamConstants.HEI_ID)
  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Factsheet"})
  public ResponseEntity<ForwardEwpApiResponseWithData<FactsheetResponseV1>> institutionsGet(
      @Valid @ParameterObject @RequestParam FactsheetRequestDto requestDto)
      throws EwpClientErrorException {
    return getFactsheet(requestDto);
  }

  // NOTE: currently only allows one HEI ID each time
  private ResponseEntity<ForwardEwpApiResponseWithData<FactsheetResponseV1>> getFactsheet(
      FactsheetRequestDto requestDto) throws EwpClientErrorException {
    EwpSuccessOperationResult<FactsheetResponseV1> institutionsResponse =
        client.findByHeiId(requestDto.getHeiId());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(institutionsResponse);
  }
}
