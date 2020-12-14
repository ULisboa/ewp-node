package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.courses;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class CoursesRequestDto {

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

  @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID)
  @Parameter(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID,
      description =
          "Must be set if no " + ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE + " is provided.")
  @Schema(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID,
      description =
          "Must be set if no " + ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE + " is provided.")
  private List<String> losIds = new ArrayList<>();

  @ParamName(value = ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE)
  @Parameter(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE,
      description =
          "Must be set if no " + ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID + " is provided.")
  @Schema(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_CODE,
      description =
          "Must be set if no " + ForwardEwpApiParamConstants.PARAM_NAME_LOS_ID + " is provided.")
  private List<String> losCodes = new ArrayList<>();

  @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_LOIS_BEFORE)
  @Parameter(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_BEFORE,
      description = "Look up LOIS before a given date")
  @Schema(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_BEFORE,
      description = "Look up LOIS before a given date")
  @DateTimeFormat(iso = DATE)
  private LocalDate loisBefore;

  @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AFTER)
  @Parameter(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AFTER,
      description = "Look up LOIS after a given date")
  @Schema(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOIS_AFTER,
      description = "Look up LOIS after a given date")
  @DateTimeFormat(iso = DATE)
  private LocalDate loisAfter;

  @ParamName(ForwardEwpApiParamConstants.PARAM_NAME_LOS_AT_DATE)
  @Parameter(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_AT_DATE,
      description = "Look up LOS at a given date")
  @Schema(
      name = ForwardEwpApiParamConstants.PARAM_NAME_LOS_AT_DATE,
      description = "Look up LOS at a given date")
  @DateTimeFormat(iso = DATE)
  private LocalDate losAtDate;

  public String getHeiId() {
    return heiId;
  }

  public void setHeiId(String heiId) {
    this.heiId = heiId;
  }

  public List<String> getLosIds() {
    return losIds;
  }

  public void setLosIds(List<String> losIds) {
    this.losIds = losIds;
  }

  public List<String> getLosCodes() {
    return losCodes;
  }

  public void setLosCodes(List<String> losCodes) {
    this.losCodes = losCodes;
  }

  public LocalDate getLoisBefore() {
    return loisBefore;
  }

  public void setLoisBefore(LocalDate loisBefore) {
    this.loisBefore = loisBefore;
  }

  public LocalDate getLoisAfter() {
    return loisAfter;
  }

  public void setLoisAfter(LocalDate loisAfter) {
    this.loisAfter = loisAfter;
  }

  public LocalDate getLosAtDate() {
    return losAtDate;
  }

  public void setLosAtDate(LocalDate losAtDate) {
    this.losAtDate = losAtDate;
  }
}
