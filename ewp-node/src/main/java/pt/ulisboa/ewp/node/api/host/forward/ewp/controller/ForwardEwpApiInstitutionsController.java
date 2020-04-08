package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

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
import pt.ulisboa.ewp.node.client.ewp.EwpInstitutionsClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientResponseAuthenticationFailedException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientUnknownErrorResponseException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import eu.erasmuswithoutpaper.api.institutions.InstitutionsResponse;
import io.swagger.v3.oas.annotations.Operation;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "institutions")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
@Validated
public class ForwardEwpApiInstitutionsController extends AbstractForwardEwpApiController {

  @Autowired private EwpInstitutionsClient institutionClient;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> institutionsGet(
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID) String heiId)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {
    return getInstitution(heiId);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> institutionsPost(
      @RequestParam(value = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID) String heiId)
      throws EwpClientErrorResponseException, EwpClientResponseAuthenticationFailedException,
          EwpClientUnknownErrorResponseException, EwpClientProcessorException {
    return getInstitution(heiId);
  }

  // NOTE: currently only allows one HEI ID each time
  private ResponseEntity<?> getInstitution(String heiId)
      throws EwpClientResponseAuthenticationFailedException, EwpClientProcessorException,
          EwpClientUnknownErrorResponseException, EwpClientErrorResponseException {
    EwpSuccessOperationResult<InstitutionsResponse> institutionsResponse =
        institutionClient.find(heiId);
    return createResponseEntityFromOperationResult(institutionsResponse);
  }
}
