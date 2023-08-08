package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.tors;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.utils.bean.ParamName;

public class ForwardEwpApiIncomingMobilityToRsGetRequestDto {

  @ParamName(EwpApiParamConstants.RECEIVING_HEI_ID)
  @NotNull
  @Size(min = 1)
  private String receivingHeiId;

  @ParamName(value = EwpApiParamConstants.OMOBILITY_ID)
  private List<String> omobilityIds = new ArrayList<>();

  public String getReceivingHeiId() {
    return receivingHeiId;
  }

  public void setReceivingHeiId(String receivingHeiId) {
    this.receivingHeiId = receivingHeiId;
  }

  public List<String> getOmobilityIds() {
    return omobilityIds;
  }

  public void setOmobilityIds(List<String> omobilityIds) {
    this.omobilityIds = omobilityIds;
  }
}
