package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"maxOmobilityIds"})
@XmlRootElement(name = "omobility-cnr-api-specification-response")
public class ForwardEwpApiOutgoingMobilityCnrApiSpecificationResponseDTO {

  @XmlElement(name = "max-omobility-ids", required = true)
  private int maxOmobilityIds;

  public ForwardEwpApiOutgoingMobilityCnrApiSpecificationResponseDTO() {
  }

  public ForwardEwpApiOutgoingMobilityCnrApiSpecificationResponseDTO(
      int maxOmobilityIds) {
    this.maxOmobilityIds = maxOmobilityIds;
  }

  public int getMaxOmobilityIds() {
    return maxOmobilityIds;
  }

  public void setMaxOmobilityIds(int maxOmobilityIds) {
    this.maxOmobilityIds = maxOmobilityIds;
  }
}
