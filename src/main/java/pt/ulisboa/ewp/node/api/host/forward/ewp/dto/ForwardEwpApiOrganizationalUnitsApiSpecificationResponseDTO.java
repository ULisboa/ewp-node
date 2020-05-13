package pt.ulisboa.ewp.node.api.host.forward.ewp.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
