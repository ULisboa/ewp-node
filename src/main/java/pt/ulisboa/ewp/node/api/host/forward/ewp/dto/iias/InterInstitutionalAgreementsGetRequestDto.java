package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class InterInstitutionalAgreementsGetRequestDto {

  @ParamName(EwpApiParamConstants.HEI_ID)
  @NotNull
  @Size(min = 1)
  private String heiId;

  @ParamName(value = EwpApiParamConstants.IIA_ID)
  private List<String> iiaIds = new ArrayList<>();

  @ParamName(value = EwpApiParamConstants.IIA_CODE)
  private List<String> iiaCodes = new ArrayList<>();

  private Boolean sendPdf;

  public String getHeiId() {
    return heiId;
  }

  public void setHeiId(String heiId) {
    this.heiId = heiId;
  }

  public List<String> getIiaIds() {
    return iiaIds;
  }

  public void setIiaIds(List<String> iiaIds) {
    this.iiaIds = iiaIds;
  }

  public List<String> getIiaCodes() {
    return iiaCodes;
  }

  public void setIiaCodes(List<String> iiaCodes) {
    this.iiaCodes = iiaCodes;
  }

  public Boolean getSendPdf() {
    return sendPdf;
  }

  public void setSendPdf(Boolean sendPdf) {
    this.sendPdf = sendPdf;
  }
}
