package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ounits;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class OrganizationalUnitsRequestDto {

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

  @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID)
  @Parameter(
      name = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID,
      description =
          "Must be set if no "
              + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE
              + " is provided.")
  @Schema(
      name = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID,
      description =
          "Must be set if no "
              + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE
              + " is provided.")
  private List<String> organizationalUnitIds = new ArrayList<>();

  @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE)
  @Parameter(
      name = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE,
      description =
          "Must be set if no " + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID + " is provided.")
  @Schema(
      name = ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_CODE,
      description =
          "Must be set if no " + ForwardEwpApiParamConstants.PARAM_NAME_OUNIT_ID + " is provided.")
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
