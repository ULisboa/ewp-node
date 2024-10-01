package pt.ulisboa.ewp.node.api.host.forward.ewp.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"heiIds"})
@XmlRootElement(name = "hei-ids")
public class ForwardEwpApiHeiIdsResponseDTO {

  @XmlElement(name = "hei-id", required = true)
  private Collection<String> heiIds;

  public ForwardEwpApiHeiIdsResponseDTO() {}

  public ForwardEwpApiHeiIdsResponseDTO(Collection<String> heiIds) {
    this.heiIds = heiIds;
  }

  public Collection<String> getHeiIds() {
    return heiIds;
  }

  public void setHeiIds(Collection<String> heiIds) {
    this.heiIds = heiIds;
  }
}
