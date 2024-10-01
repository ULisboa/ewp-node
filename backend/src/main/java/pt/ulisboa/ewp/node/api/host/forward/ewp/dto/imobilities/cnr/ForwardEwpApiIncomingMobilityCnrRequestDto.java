package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.cnr;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiIncomingMobilityCnrRequestDto {

  @ParamName(EwpApiParamConstants.SENDING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String sendingHeiId;

  @ParamName(EwpApiParamConstants.RECEIVING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String receivingHeiId;

  @ParamName(value = EwpApiParamConstants.OMOBILITY_ID)
  private List<String> outgoingMobilityIds = new ArrayList<>();

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

  public List<String> getOutgoingMobilityIds() {
    return outgoingMobilityIds;
  }

  public void setOutgoingMobilityIds(List<String> outgoingMobilityIds) {
    this.outgoingMobilityIds = outgoingMobilityIds;
  }
}
