package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval.cnr;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiInterInstitutionalAgreementApprovalCnrRequestDto {

  @ParamName(EwpApiParamConstants.APPROVING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String approvingHeiId;

  @ParamName(EwpApiParamConstants.PARTNER_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String partnerHeiId;

  @ParamName(EwpApiParamConstants.OWNER_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String ownerId;

  @ParamName(value = EwpApiParamConstants.IIA_ID)
  @NotNull
  @Size(min = 1)
  private String iiaId;

  public String getApprovingHeiId() {
    return approvingHeiId;
  }

  public void setApprovingHeiId(String approvingHeiId) {
    this.approvingHeiId = approvingHeiId;
  }

  public String getPartnerHeiId() {
    return partnerHeiId;
  }

  public void setPartnerHeiId(String partnerHeiId) {
    this.partnerHeiId = partnerHeiId;
  }

  public String getOwnerId() {
    return ownerId;
  }

  public void setOwnerId(String ownerId) {
    this.ownerId = ownerId;
  }

  public String getIiaId() {
    return iiaId;
  }

  public void setIiaId(String iiaId) {
    this.iiaId = iiaId;
  }
}
