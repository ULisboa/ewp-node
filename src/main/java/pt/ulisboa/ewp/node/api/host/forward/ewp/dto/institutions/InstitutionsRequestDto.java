package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.institutions;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class InstitutionsRequestDto {

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
