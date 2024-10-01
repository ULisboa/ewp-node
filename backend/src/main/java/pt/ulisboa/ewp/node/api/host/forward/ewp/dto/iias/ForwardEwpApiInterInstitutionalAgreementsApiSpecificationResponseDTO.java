package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"maxIiaIds", "maxIiaCodes"})
@XmlRootElement(name = "iias-api-specification-response")
public class ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO {

  @XmlElement(name = "max-iia-ids", required = true)
  private int maxIiaIds;

  @XmlElement(name = "max-iia-codes", required = true)
  private int maxIiaCodes;

  public ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO() {}

  public ForwardEwpApiInterInstitutionalAgreementsApiSpecificationResponseDTO(
      int maxIiaIds, int maxIiaCodes) {
    this.maxIiaIds = maxIiaIds;
    this.maxIiaCodes = maxIiaCodes;
  }

  public int getMaxIiaIds() {
    return maxIiaIds;
  }

  public void setMaxIiaIds(int maxIiaIds) {
    this.maxIiaIds = maxIiaIds;
  }

  public int getMaxIiaCodes() {
    return maxIiaCodes;
  }

  public void setMaxIiaCodes(int maxIiaCodes) {
    this.maxIiaCodes = maxIiaCodes;
  }
}
