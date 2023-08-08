package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.cnr;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiOutgoingMobilityCnrRequestDto {

  @ParamName(EwpApiParamConstants.SENDING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String sendingHeiId;

  @ParamName(EwpApiParamConstants.SENDING_OUNIT_ID)
  private String sendingOunitId;

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

  public String getSendingOunitId() {
    return sendingOunitId;
  }

  public void setSendingOunitId(String sendingOunitId) {
    this.sendingOunitId = sendingOunitId;
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
