package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias.hash.calculation.request;

import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia;
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
    propOrder = {"iias"})
@XmlRootElement(name = "iia-hashes-calculation-request")
public class ForwardEwpApiIiaHashesCalculationV7RequestDTO {

  @XmlElementWrapper(name = "iias")
  @XmlElement(name = "iia", required = true)
  private List<Iia> iias;

  public ForwardEwpApiIiaHashesCalculationV7RequestDTO() {
  }

  public ForwardEwpApiIiaHashesCalculationV7RequestDTO(List<Iia> iias) {
    this.iias = iias;
  }

  public List<Iia> getIias() {
    return iias;
  }

  public void setIias(List<Iia> iias) {
    this.iias = iias;
  }
}
