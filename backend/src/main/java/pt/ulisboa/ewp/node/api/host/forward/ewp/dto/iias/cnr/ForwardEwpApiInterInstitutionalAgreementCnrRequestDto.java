package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.cnr;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiInterInstitutionalAgreementCnrRequestDto {

  @ParamName(EwpApiParamConstants.NOTIFIER_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String notifierHeiId;

  @ParamName(EwpApiParamConstants.NOTIFIER_OUNIT_ID)
  private String notifierOunitId;

  @ParamName(EwpApiParamConstants.PARTNER_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String partnerHeiId;

  @ParamName(value = EwpApiParamConstants.IIA_ID)
  private List<String> iiaIds = new ArrayList<>();

  public String getNotifierHeiId() {
    return notifierHeiId;
  }

  public void setNotifierHeiId(String notifierHeiId) {
    this.notifierHeiId = notifierHeiId;
  }

  public String getNotifierOunitId() {
    return notifierOunitId;
  }

  public void setNotifierOunitId(String notifierOunitId) {
    this.notifierOunitId = notifierOunitId;
  }

  public String getPartnerHeiId() {
    return partnerHeiId;
  }

  public void setPartnerHeiId(String partnerHeiId) {
    this.partnerHeiId = partnerHeiId;
  }

  public List<String> getIiaIds() {
    return iiaIds;
  }

  public void setIiaIds(List<String> iiaIds) {
    this.iiaIds = iiaIds;
  }
}
