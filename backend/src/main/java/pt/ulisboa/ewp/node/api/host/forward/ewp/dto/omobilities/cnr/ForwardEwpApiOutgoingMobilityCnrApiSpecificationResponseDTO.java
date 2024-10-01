package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.cnr;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

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
