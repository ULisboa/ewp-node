package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.tors;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiIncomingMobilityToRsIndexRequestDto {

  @ParamName(EwpApiParamConstants.RECEIVING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String receivingHeiId;

  @ParamName(value = EwpApiParamConstants.SENDING_HEI_ID)
  private List<String> sendingHeiIds = new ArrayList<>();

  @ParamName(value = EwpApiParamConstants.MODIFIED_SINCE)
  private ZonedDateTime modifiedSince;

  public String getReceivingHeiId() {
    return receivingHeiId;
  }

  public void setReceivingHeiId(String receivingHeiId) {
    this.receivingHeiId = receivingHeiId;
  }

  public List<String> getSendingHeiIds() {
    return sendingHeiIds;
  }

  public void setSendingHeiIds(List<String> sendingHeiIds) {
    this.sendingHeiIds = sendingHeiIds;
  }

  public ZonedDateTime getModifiedSince() {
    return modifiedSince;
  }

  public void setModifiedSince(ZonedDateTime modifiedSince) {
    this.modifiedSince = modifiedSince;
  }
}
