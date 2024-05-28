package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.cnr;

import java.util.ArrayList;
import java.util.Collection;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
