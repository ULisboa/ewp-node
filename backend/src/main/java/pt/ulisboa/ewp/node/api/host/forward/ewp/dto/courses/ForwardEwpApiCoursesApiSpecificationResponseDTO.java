package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.courses;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"maxLosIds", "maxLosCodes"})
@XmlRootElement(name = "courses-api-specification-response")
public class ForwardEwpApiCoursesApiSpecificationResponseDTO {

  @XmlElement(name = "max-los-ids", required = true)
  private int maxLosIds;

  @XmlElement(name = "max-los-codes", required = true)
  private int maxLosCodes;

  public ForwardEwpApiCoursesApiSpecificationResponseDTO() {}

  public ForwardEwpApiCoursesApiSpecificationResponseDTO(int maxLosIds, int maxLosCodes) {
    this.maxLosIds = maxLosIds;
    this.maxLosCodes = maxLosCodes;
  }

  public int getMaxLosIds() {
    return maxLosIds;
  }

  public void setMaxLosIds(int maxLosIds) {
    this.maxLosIds = maxLosIds;
  }

  public int getMaxLosCodes() {
    return maxLosCodes;
  }

  public void setMaxLosCodes(int maxLosCodes) {
    this.maxLosCodes = maxLosCodes;
  }
}
