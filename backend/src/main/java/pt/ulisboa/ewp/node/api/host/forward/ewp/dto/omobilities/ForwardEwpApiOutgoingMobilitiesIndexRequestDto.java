package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiOutgoingMobilitiesIndexRequestDto {

  @ParamName(EwpApiParamConstants.SENDING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String sendingHeiId;

  @ParamName(value = EwpApiParamConstants.RECEIVING_HEI_ID)
  private List<String> receivingHeiIds = new ArrayList<>();

  @ParamName(EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID)
  private String receivingAcademicYearId;

  @ParamName(value = EwpApiParamConstants.MODIFIED_SINCE)
  private ZonedDateTime modifiedSince;

  public String getSendingHeiId() {
    return sendingHeiId;
  }

  public void setSendingHeiId(String sendingHeiId) {
    this.sendingHeiId = sendingHeiId;
  }

  public List<String> getReceivingHeiIds() {
    return receivingHeiIds;
  }

  public void setReceivingHeiIds(List<String> receivingHeiIds) {
    this.receivingHeiIds = receivingHeiIds;
  }

  public String getReceivingAcademicYearId() {
    return receivingAcademicYearId;
  }

  public void setReceivingAcademicYearId(String receivingAcademicYearId) {
    this.receivingAcademicYearId = receivingAcademicYearId;
  }

  public ZonedDateTime getModifiedSince() {
    return modifiedSince;
  }

  public void setModifiedSince(ZonedDateTime modifiedSince) {
    this.modifiedSince = modifiedSince;
  }
}
