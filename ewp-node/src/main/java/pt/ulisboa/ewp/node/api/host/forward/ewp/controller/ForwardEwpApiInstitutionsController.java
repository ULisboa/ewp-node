package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import eu.erasmuswithoutpaper.api.institutions.InstitutionsResponse;
import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpInstitutionsClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "institutions")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiInstitutionsController extends AbstractForwardEwpApiController {

  @Autowired private EwpInstitutionsClient institutionClient;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> institutionsGet(@Valid @RequestParam InstitutionsRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getInstitution(requestDto.getHeiId());
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> institutionsPost(@Valid @RequestParam InstitutionsRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getInstitution(requestDto.getHeiId());
  }

  // NOTE: currently only allows one HEI ID each time
  private ResponseEntity<?> getInstitution(String heiId) throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<InstitutionsResponse> institutionsResponse =
        institutionClient.find(heiId);
    return createResponseEntityFromOperationResult(institutionsResponse);
  }

  private static class InstitutionsRequestDto {

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID)
    @NotNull
    @Size(min = 1)
    private String heiId;

    public String getHeiId() {
      return heiId;
    }

    public void setHeiId(String heiId) {
      this.heiId = heiId;
    }
  }
}
