package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.factsheet;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class FactsheetRequestDto {

  @ParamName(EwpApiParamConstants.HEI_ID)
  @Parameter(name = EwpApiParamConstants.HEI_ID, description = "HEI ID (SCHAC code) to look up")
  @Schema(name = EwpApiParamConstants.HEI_ID, description = "HEI ID (SCHAC code) to look up")
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
