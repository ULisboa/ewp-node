package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias;

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
    propOrder = {"iias"})
@XmlRootElement(name = "iias")
public class ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto {

  @XmlElement(name = "iia", required = true)
  private Collection<ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto> iias = new ArrayList<>();

  public ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto() {
  }

  public ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto(
      Collection<ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto> iias) {
    this.iias = iias;
  }

  public Collection<ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto> getIias() {
    return iias;
  }

  public void setIias(
      Collection<ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto> iias) {
    this.iias = iias;
  }
}
