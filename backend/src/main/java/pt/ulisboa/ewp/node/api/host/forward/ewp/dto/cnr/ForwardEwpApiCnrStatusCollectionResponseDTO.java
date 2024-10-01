package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.cnr;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.Collection;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"cnrStatuses"})
@XmlRootElement(name = "cnr-statuses")
public class ForwardEwpApiCnrStatusCollectionResponseDTO {

  @XmlElement(name = "cnr-status", required = true)
  private Collection<ForwardEwpApiCnrStatusResponseDTO> cnrStatuses = new ArrayList<>();

  public ForwardEwpApiCnrStatusCollectionResponseDTO() {}

  public ForwardEwpApiCnrStatusCollectionResponseDTO(
      Collection<ForwardEwpApiCnrStatusResponseDTO> cnrStatuses) {
    this.cnrStatuses = cnrStatuses;
  }

  public Collection<ForwardEwpApiCnrStatusResponseDTO> getCnrStatuses() {
    return cnrStatuses;
  }

  public void setCnrStatuses(Collection<ForwardEwpApiCnrStatusResponseDTO> cnrStatuses) {
    this.cnrStatuses = cnrStatuses;
  }
}
