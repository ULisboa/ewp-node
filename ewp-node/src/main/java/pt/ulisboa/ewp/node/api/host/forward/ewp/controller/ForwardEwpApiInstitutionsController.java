package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import eu.erasmuswithoutpaper.api.institutions.InstitutionsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
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
      tags = {"Institutions"})
  public ResponseEntity<ForwardEwpApiResponseWithData<InstitutionsResponse>> institutionsGet(
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
  public ResponseEntity<ForwardEwpApiResponseWithData<InstitutionsResponse>> institutionsPost(
      @Valid InstitutionsRequestDto requestDto) throws AbstractEwpClientErrorException {
    return getInstitution(requestDto.getHeiId());
  }

  // NOTE: currently only allows one HEI ID each time
  private ResponseEntity<ForwardEwpApiResponseWithData<InstitutionsResponse>> getInstitution(
      String heiId) throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<InstitutionsResponse> institutionsResponse =
        institutionClient.find(heiId);
    return createResponseEntityFromOperationResult(institutionsResponse);
  }

  private static class InstitutionsRequestDto {

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID)
    @Parameter(
        name = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID,
        description = "HEI ID (SCHAC code) to look up")
    @Schema(
        name = ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID,
        description = "HEI ID (SCHAC code) to look up")
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
