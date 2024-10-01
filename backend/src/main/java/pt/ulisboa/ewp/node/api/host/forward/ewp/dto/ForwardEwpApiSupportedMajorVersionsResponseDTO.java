package pt.ulisboa.ewp.node.api.host.forward.ewp.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"supportedMajorVersions"})
@XmlRootElement(name = "supported-major-versions-response")
public class ForwardEwpApiSupportedMajorVersionsResponseDTO {

  @XmlElementWrapper(name = "supported-major-versions")
  @XmlElement(name = "major-version")
  private List<Integer> supportedMajorVersions;

  public ForwardEwpApiSupportedMajorVersionsResponseDTO() {}

  public ForwardEwpApiSupportedMajorVersionsResponseDTO(List<Integer> supportedMajorVersions) {
    this.supportedMajorVersions = supportedMajorVersions;
  }

  public List<Integer> getSupportedMajorVersions() {
    return supportedMajorVersions;
  }

  public void setSupportedMajorVersions(List<Integer> supportedMajorVersions) {
    this.supportedMajorVersions = supportedMajorVersions;
  }
}
