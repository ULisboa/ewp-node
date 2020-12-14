package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.courses.replication;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import java.time.ZonedDateTime;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.springframework.format.annotation.DateTimeFormat;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class SimpleCourseReplicationRequestDto {

  @ParamName(EwpApiParamConstants.HEI_ID)
  @Parameter(name = EwpApiParamConstants.HEI_ID, description = "HEI ID (SCHAC code) to look up")
  @Schema(name = EwpApiParamConstants.HEI_ID, description = "HEI ID (SCHAC code) to look up")
  @NotNull
  @Size(min = 1)
  private String heiId;

  @ParamName(EwpApiParamConstants.MODIFIED_SINCE)
  @Parameter(
      name = EwpApiParamConstants.MODIFIED_SINCE,
      description = "Find LOS modified since a given date")
  @Schema(
      name = EwpApiParamConstants.MODIFIED_SINCE,
      description = "Find LOS modified since a given date")
  @DateTimeFormat(iso = DATE_TIME)
  private ZonedDateTime modifiedSince;

  public String getHeiId() {
    return heiId;
  }

  public void setHeiId(String heiId) {
    this.heiId = heiId;
  }

  public ZonedDateTime getModifiedSince() {
    return modifiedSince;
  }

  public void setModifiedSince(ZonedDateTime modifiedSince) {
    this.modifiedSince = modifiedSince;
  }
}
