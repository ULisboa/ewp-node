package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.util.ArrayList;
import java.util.List;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiOutgoingMobilitiesGetRequestDto {

  @ParamName(EwpApiParamConstants.SENDING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String sendingHeiId;

  @ParamName(value = EwpApiParamConstants.OMOBILITY_ID)
  private List<String> omobilityIds = new ArrayList<>();

  public String getSendingHeiId() {
    return sendingHeiId;
  }

  public void setSendingHeiId(String sendingHeiId) {
    this.sendingHeiId = sendingHeiId;
  }

  public List<String> getOmobilityIds() {
    return omobilityIds;
  }

  public void setOmobilityIds(List<String> omobilityIds) {
    this.omobilityIds = omobilityIds;
  }
}
