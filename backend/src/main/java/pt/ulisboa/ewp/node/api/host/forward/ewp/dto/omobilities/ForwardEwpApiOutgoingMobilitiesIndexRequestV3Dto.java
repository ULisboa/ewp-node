package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiOutgoingMobilitiesIndexRequestV3Dto {

  @ParamName(EwpApiParamConstants.SENDING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String sendingHeiId;

  @ParamName(value = EwpApiParamConstants.RECEIVING_HEI_ID)
  private String receivingHeiId;

  @ParamName(EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID)
  private String receivingAcademicYearId;

  @ParamName(value = EwpApiParamConstants.MODIFIED_SINCE)
  private ZonedDateTime modifiedSince;

  @ParamName(EwpApiParamConstants.GLOBAL_ID)
  private String globalId;

  @ParamName(EwpApiParamConstants.ACTIVITY_ATTRIBUTES)
  private String activityAttributes;

  public String getSendingHeiId() {
    return sendingHeiId;
  }

  public void setSendingHeiId(String sendingHeiId) {
    this.sendingHeiId = sendingHeiId;
  }

  public String getReceivingHeiId() {
    return receivingHeiId;
  }

  public void setReceivingHeiId(String receivingHeiId) {
    this.receivingHeiId = receivingHeiId;
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

  public String getGlobalId() {
    return globalId;
  }

  public void setGlobalId(String globalId) {
    this.globalId = globalId;
  }

  public String getActivityAttributes() {
    return activityAttributes;
  }

  public void setActivityAttributes(String activityAttributes) {
    this.activityAttributes = activityAttributes;
  }
}
