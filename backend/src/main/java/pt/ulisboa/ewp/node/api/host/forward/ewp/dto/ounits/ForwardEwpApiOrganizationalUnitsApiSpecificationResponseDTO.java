package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ounits;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"maxOunitIds", "maxOunitCodes"})
@XmlRootElement(name = "ounits-api-specification-response")
public class ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO {

  @XmlElement(name = "max-ounit-ids", required = true)
  private int maxOunitIds;

  @XmlElement(name = "max-ounit-codes", required = true)
  private int maxOunitCodes;

  public ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO() {}

  public ForwardEwpApiOrganizationalUnitsApiSpecificationResponseDTO(
      int maxOunitIds, int maxOunitCodes) {
    this.maxOunitIds = maxOunitIds;
    this.maxOunitCodes = maxOunitCodes;
  }

  public int getMaxOunitIds() {
    return maxOunitIds;
  }

  public void setMaxOunitIds(int maxOunitIds) {
    this.maxOunitIds = maxOunitIds;
  }

  public int getMaxOunitCodes() {
    return maxOunitCodes;
  }

  public void setMaxOunitCodes(int maxOunitCodes) {
    this.maxOunitCodes = maxOunitCodes;
  }
}
