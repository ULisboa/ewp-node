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
    propOrder = {"iias", "rawXmlInBase64"})
@XmlRootElement(
    name = "iias",
    namespace = "https://github.com/ULisboa/ewp-node/tree/master/api/forward/ewp/iias/v6")
public class ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto {

  @XmlElement(
      name = "iia-with-hash-validation",
      required = true,
      namespace = "https://github.com/ULisboa/ewp-node/tree/master/api/forward/ewp/iias/v6")
  private Collection<ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto> iias =
      new ArrayList<>();

  @XmlElement(
      name = "raw-xml-base64",
      namespace = "https://github.com/ULisboa/ewp-node/tree/master/api/forward/ewp/iias/v6")
  private byte[] rawXmlInBase64;

  public ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto() {
  }

  public ForwardEwpApiInterInstitutionalAgreementsV6GetResponseDto(
      Collection<ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto> iias,
      byte[] rawXmlInBase64) {
    this.iias = iias;
    this.rawXmlInBase64 = rawXmlInBase64;
  }

  public Collection<ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto> getIias() {
    return iias;
  }

  public void setIias(
      Collection<ForwardEwpApiInterInstitutionalAgreementV6WithHashValidationResponseDto> iias) {
    this.iias = iias;
  }

  public byte[] getRawXmlInBase64() {
    return rawXmlInBase64;
  }

  public void setRawXmlInBase64(byte[] rawXmlInBase64) {
    this.rawXmlInBase64 = rawXmlInBase64;
  }
}
