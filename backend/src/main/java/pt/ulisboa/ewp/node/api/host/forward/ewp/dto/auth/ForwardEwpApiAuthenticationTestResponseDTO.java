package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.auth;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"hostCode"})
@XmlRootElement(name = "authentication-test-response")
public class ForwardEwpApiAuthenticationTestResponseDTO {

  @XmlElement(name = "host-code", required = true)
  private String hostCode;

  public String getHostCode() {
    return hostCode;
  }

  public void setHostCode(String hostCode) {
    this.hostCode = hostCode;
  }
}
