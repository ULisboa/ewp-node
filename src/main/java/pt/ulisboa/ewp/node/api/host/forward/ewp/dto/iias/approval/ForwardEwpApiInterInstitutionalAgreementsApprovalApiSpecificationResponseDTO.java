package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.approval;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"maxIiaIds"})
@XmlRootElement(name = "iias-approval-api-specification-response")
public class ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO {

  @XmlElement(name = "max-iia-ids", required = true)
  private int maxIiaIds;

  public ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO() {
  }

  public ForwardEwpApiInterInstitutionalAgreementsApprovalApiSpecificationResponseDTO(
      int maxIiaIds) {
    this.maxIiaIds = maxIiaIds;
  }

  public int getMaxIiaIds() {
    return maxIiaIds;
  }

  public void setMaxIiaIds(int maxIiaIds) {
    this.maxIiaIds = maxIiaIds;
  }
}
