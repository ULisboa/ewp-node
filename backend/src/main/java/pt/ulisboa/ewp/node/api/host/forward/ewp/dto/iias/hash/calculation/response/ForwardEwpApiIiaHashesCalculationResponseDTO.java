package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.hash.calculation.response;

import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"hashes"})
@XmlRootElement(name = "hashes")
public class ForwardEwpApiIiaHashesCalculationResponseDTO {

  @XmlElement(name = "hashes", required = true)
  private Collection<String> hashes;

  public ForwardEwpApiIiaHashesCalculationResponseDTO() {
  }

  public ForwardEwpApiIiaHashesCalculationResponseDTO(Collection<String> hashes) {
    this.hashes = hashes;
  }

  public Collection<String> getHashes() {
    return hashes;
  }

  public void setHashes(Collection<String> hashes) {
    this.hashes = hashes;
  }
}
