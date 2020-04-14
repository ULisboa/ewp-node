package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import eu.erasmuswithoutpaper.api.ounits.OunitsResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import java.util.ArrayList;
import java.util.List;
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
import pt.ulisboa.ewp.node.client.ewp.EwpOrganizationalUnitsClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "ounits")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiOrganizationalUnitsController extends AbstractForwardEwpApiController {

  @Autowired private EwpOrganizationalUnitsClient organizationalUnitClient;

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Organizational Units Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> organizationalUnitsGet(
      @Valid @RequestParam OrganizationalUnitsRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getOrganizationalUnits(
        requestDto.getHeiId(),
        requestDto.getOrganizationalUnitIds(),
        requestDto.getOrganizationalUnitCodes());
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Organizational Units Forward API.",
      tags = {"Forward EWP API"})
  public ResponseEntity<?> organizationalUnitsPost(
      @Valid @RequestParam OrganizationalUnitsRequestDto requestDto)
      throws AbstractEwpClientErrorException {
    return getOrganizationalUnits(
        requestDto.getHeiId(),
        requestDto.getOrganizationalUnitIds(),
        requestDto.getOrganizationalUnitCodes());
  }

  // NOTE: currently only allows to obtain by ounit IDs or ounit codes (not both simultaneously)
  private ResponseEntity<?> getOrganizationalUnits(
      String heiId, List<String> organizationalUnitIds, List<String> organizationalUnitCodes)
      throws AbstractEwpClientErrorException {
    EwpSuccessOperationResult<OunitsResponse> ounitsResponse;
    if (!organizationalUnitIds.isEmpty()) {
      ounitsResponse = organizationalUnitClient.findByOunitIds(heiId, organizationalUnitIds);
    } else {
      ounitsResponse = organizationalUnitClient.findByOunitCodes(heiId, organizationalUnitCodes);
    }
    return createResponseEntityFromOperationResult(ounitsResponse);
  }

  private static class OrganizationalUnitsRequestDto {

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_HEI_ID)
    @NotNull
    @Size(min = 1)
    private String heiId;

    @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID)
    @Parameter(
        description =
            "Must be set if no "
                + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE
                + " is provided.")
    private List<String> organizationalUnitIds = new ArrayList<>();

    @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE)
    @Parameter(
        description =
            "Must be set if no "
                + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID
                + " is provided.")
    private List<String> organizationalUnitCodes = new ArrayList<>();

    public String getHeiId() {
      return heiId;
    }

    public void setHeiId(String heiId) {
      this.heiId = heiId;
    }

    public List<String> getOrganizationalUnitIds() {
      return organizationalUnitIds;
    }

    public void setOrganizationalUnitIds(List<String> organizationalUnitIds) {
      this.organizationalUnitIds = organizationalUnitIds;
    }

    public List<String> getOrganizationalUnitCodes() {
      return organizationalUnitCodes;
    }

    public void setOrganizationalUnitCodes(List<String> organizationalUnitCodes) {
      this.organizationalUnitCodes = organizationalUnitCodes;
    }
  }
}
