package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias;

import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia;
import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"iia", "hashValidation"})
@XmlRootElement(
    name = "iia-with-hash-validation",
    namespace = "https://github.com/ULisboa/ewp-node/tree/master/api/forward/ewp/iias/v7")
public class ForwardEwpApiInterInstitutionalAgreementV7WithHashValidationResponseDto {

  @XmlElement(
      name = "iia",
      required = true,
      namespace =
          "https://github.com/erasmus-without-paper/ewp-specs-api-iias/blob/stable-v7/endpoints/get-response.xsd")
  private Iia iia;

  @XmlElement(
      name = "hash-validation",
      required = true,
      namespace =
          "https://github.com/ULisboa/ewp-node/tree/master/api/forward/ewp/iias/hash/validation")
  private ForwardEwpApiInterInstitutionalAgreementHashValidationDto hashValidation;

  public ForwardEwpApiInterInstitutionalAgreementV7WithHashValidationResponseDto() {}

  public ForwardEwpApiInterInstitutionalAgreementV7WithHashValidationResponseDto(
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
