package pt.ulisboa.ewp.node.api.host.forward.ewp.dto;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

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
