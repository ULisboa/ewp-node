package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.files;

import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class FileRequestDto {

  @ParamName(EwpApiParamConstants.HEI_ID)
  @Parameter(name = EwpApiParamConstants.HEI_ID, description = "HEI ID (SCHAC code) to look up")
  @Schema(name = EwpApiParamConstants.HEI_ID, description = "HEI ID (SCHAC code) to look up")
  @NotNull
  @Size(min = 1)
  private String heiId;

  @ParamName(EwpApiParamConstants.FILE_ID)
  @Parameter(name = EwpApiParamConstants.FILE_ID, description = "File ID to look up")
  @Schema(name = EwpApiParamConstants.FILE_ID, description = "File ID to look up")
  @NotNull
  @Size(min = 1)
  private String fileId;

  public String getHeiId() {
    return heiId;
  }

  public void setHeiId(String heiId) {
    this.heiId = heiId;
  }

  public String getFileId() {
    return fileId;
  }

  public void setFileId(String fileId) {
    this.fileId = fileId;
  }
}
