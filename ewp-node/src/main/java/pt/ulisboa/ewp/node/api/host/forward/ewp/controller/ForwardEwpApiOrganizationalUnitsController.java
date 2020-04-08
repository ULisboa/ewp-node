package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpOrganizationalUnitsClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientResponseAuthenticationFailedException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientUnknownErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import eu.erasmuswithoutpaper.api.ounits.OunitsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "ounits")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
@Validated
public class ForwardEwpApiOrganizationalUnitsController extends AbstractForwardEwpApiController {

  @Autowired private EwpOrganizationalUnitsClient organizationalUnitClient;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Organizational Units Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> organizationalUnitsGet(
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID) String heiId,
      @Parameter(
              description =
                  "Must be set if no "
                      + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE
                      + " is provided.")
          @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID, defaultValue = "")
          List<String> organizationalUnitIds,
      @Parameter(
              description =
                  "Must be set if no "
                      + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID
                      + " is provided.")
          @RequestParam(
              value = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE,
              defaultValue = "")
          List<String> organizationalUnitCodes)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {
    return getOrganizationalUnits(heiId, organizationalUnitIds, organizationalUnitCodes);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Organizational Units Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> organizationalUnitsPost(
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID) String heiId,
      @Parameter(
              description =
                  "Must be set if no "
                      + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE
                      + " is provided.")
          @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID, required = false)
          List<String> organizationalUnitIds,
      @Parameter(
              description =
                  "Must be set if no "
                      + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID
                      + " is provided.")
          @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE, required = false)
          List<String> organizationalUnitCodes)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {
    if (organizationalUnitIds == null) {
      organizationalUnitIds = new ArrayList<>();
    }
    if (organizationalUnitCodes == null) {
      organizationalUnitCodes = new ArrayList<>();
    }
    return getOrganizationalUnits(heiId, organizationalUnitIds, organizationalUnitCodes);
  }

  // NOTE: currently only allows to obtain by ounit IDs or ounit codes (not both simultaneously)
  private ResponseEntity<?> getOrganizationalUnits(
      String heiId, List<String> organizationalUnitIds, List<String> organizationalUnitCodes)
      throws EwpClientResponseAuthenticationFailedException, EwpClientProcessorException,
          EwpClientUnknownErrorResponseException, EwpClientErrorResponseException {
    EwpSuccessOperationResult<OunitsResponse> ounitsResponse;
    if (!organizationalUnitIds.isEmpty()) {
      ounitsResponse = organizationalUnitClient.findByOunitIds(heiId, organizationalUnitIds);
    } else {
      ounitsResponse = organizationalUnitClient.findByOunitCodes(heiId, organizationalUnitCodes);
    }
    return createResponseEntityFromOperationResult(ounitsResponse);
  }
}
