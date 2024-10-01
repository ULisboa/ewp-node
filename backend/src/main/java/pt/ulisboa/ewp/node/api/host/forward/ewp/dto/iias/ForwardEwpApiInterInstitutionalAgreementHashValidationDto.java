package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.iias;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"hashExtracted", "hashExpected", "valid"})
@XmlRootElement(
    name = "iia-with-hash-validation",
    namespace =
        "https://github.com/ULisboa/ewp-node/tree/master/api/forward/ewp/iias/hash/validation")
public class ForwardEwpApiInterInstitutionalAgreementHashValidationDto {

  @XmlElement(
      name = "hash-extracted",
      required = true,
      namespace =
          "https://github.com/ULisboa/ewp-node/tree/master/api/forward/ewp/iias/hash/validation")
  private String hashExtracted;

  @XmlElement(
      name = "hash-expected",
      required = true,
      namespace =
          "https://github.com/ULisboa/ewp-node/tree/master/api/forward/ewp/iias/hash/validation")
  private String hashExpected;

  @XmlElement(
      name = "valid",
      required = true,
      namespace =
          "https://github.com/ULisboa/ewp-node/tree/master/api/forward/ewp/iias/hash/validation")
  private boolean valid;

  public ForwardEwpApiInterInstitutionalAgreementHashValidationDto() {

  }

  public ForwardEwpApiInterInstitutionalAgreementHashValidationDto(String hashExtracted,
      String hashExpected, boolean valid) {
    this.hashExtracted = hashExtracted;
    this.hashExpected = hashExpected;
    this.valid = valid;
  }

  public String getHashExtracted() {
    return hashExtracted;
  }

  public void setHashExtracted(String hashExtracted) {
    this.hashExtracted = hashExtracted;
  }

  public String getHashExpected() {
    return hashExpected;
  }

  public void setHashExpected(String hashExpected) {
    this.hashExpected = hashExpected;
  }

  public boolean isValid() {
    return valid;
  }

  public void setValid(boolean valid) {
    this.valid = valid;
  }
}