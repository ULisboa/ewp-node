package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"iia", "hashValidation"})
@XmlRootElement(name = "iia-with-hash-validation")
public class ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto {

  @XmlElement(
      name = "iia",
      required = true,
      namespace =
          "https://github.com/erasmus-without-paper/ewp-specs-api-iias/blob/stable-v6/endpoints/get-response.xsd")
  private Iia iia;

  @XmlElement(name = "hash-validation", required = true)
  private ForwardEwpApiInterInstitutionalAgreementHashValidationDto hashValidation;

  public ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto() {
  }

  public ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto(
      Iia iia, ForwardEwpApiInterInstitutionalAgreementHashValidationDto hashValidation) {
    this.iia = iia;
    this.hashValidation = hashValidation;
  }

  public Iia getIia() {
    return iia;
  }

  public void setIia(Iia iia) {
    this.iia = iia;
  }

  public ForwardEwpApiInterInstitutionalAgreementHashValidationDto getHashValidation() {
    return hashValidation;
  }

  public void setHashValidation(
      ForwardEwpApiInterInstitutionalAgreementHashValidationDto hashValidation) {
    this.hashValidation = hashValidation;
  }
}
