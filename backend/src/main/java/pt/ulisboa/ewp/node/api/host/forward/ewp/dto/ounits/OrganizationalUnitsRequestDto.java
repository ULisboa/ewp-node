package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ounits;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class OrganizationalUnitsRequestDto {

  @ParamName(EwpApiParamConstants.HEI_ID)
  @Parameter(name = EwpApiParamConstants.HEI_ID, description = "HEI ID (SCHAC code) to look up")
  @Schema(name = EwpApiParamConstants.HEI_ID, description = "HEI ID (SCHAC code) to look up")
  @NotNull
  @Size(min = 1)
  private String heiId;

  @ParamName(EwpApiParamConstants.OUNIT_ID)
  @Parameter(
      name = EwpApiParamConstants.OUNIT_ID,
      description = "Must be set if no " + EwpApiParamConstants.OUNIT_CODE + " is provided.")
  @Schema(
      name = EwpApiParamConstants.OUNIT_ID,
      description = "Must be set if no " + EwpApiParamConstants.OUNIT_CODE + " is provided.")
  private List<String> organizationalUnitIds = new ArrayList<>();

  @ParamName(value = EwpApiParamConstants.OUNIT_CODE)
  @Parameter(
      name = EwpApiParamConstants.OUNIT_CODE,
      description = "Must be set if no " + EwpApiParamConstants.OUNIT_ID + " is provided.")
  @Schema(
      name = EwpApiParamConstants.OUNIT_CODE,
      description = "Must be set if no " + EwpApiParamConstants.OUNIT_ID + " is provided.")
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
