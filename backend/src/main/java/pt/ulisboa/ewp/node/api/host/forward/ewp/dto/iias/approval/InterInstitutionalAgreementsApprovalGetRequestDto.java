package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class InterInstitutionalAgreementsApprovalGetRequestDto {

  @ParamName(EwpApiParamConstants.APPROVING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String approvingHeiId;

  @ParamName(EwpApiParamConstants.OWNER_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String ownerHeiId;

  @ParamName(value = EwpApiParamConstants.IIA_ID)
  @NotEmpty
  private List<String> iiaIds = new ArrayList<>();

  @ParamName(value = EwpApiParamConstants.SEND_PDF)
  private Boolean sendPdf;

  public String getApprovingHeiId() {
    return approvingHeiId;
  }

  public void setApprovingHeiId(String approvingHeiId) {
    this.approvingHeiId = approvingHeiId;
  }

  public String getOwnerHeiId() {
    return ownerHeiId;
  }

  public void setOwnerHeiId(String ownerHeiId) {
    this.ownerHeiId = ownerHeiId;
  }

  public List<String> getIiaIds() {
    return iiaIds;
  }

  public void setIiaIds(List<String> iiaIds) {
    this.iiaIds = iiaIds;
  }

  public Boolean getSendPdf() {
    return sendPdf;
  }

  public void setSendPdf(Boolean sendPdf) {
    this.sendPdf = sendPdf;
  }
}
