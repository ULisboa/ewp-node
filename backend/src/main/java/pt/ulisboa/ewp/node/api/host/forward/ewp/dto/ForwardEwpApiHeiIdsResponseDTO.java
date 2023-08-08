package pt.ulisboa.ewp.node.api.host.forward.ewp.dto;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
