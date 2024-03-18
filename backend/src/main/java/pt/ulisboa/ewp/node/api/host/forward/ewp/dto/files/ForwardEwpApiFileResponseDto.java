package pt.ulisboa.ewp.node.api.host.forward.ewp.dto.files;

import java.util.Base64;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(
    name = "",
    propOrder = {"mediaType", "dataInBase64"})
@XmlRootElement(name = "file-response")
public class ForwardEwpApiFileResponseDto {

  @XmlElement(name = "media-type")
  private String mediaType;

  @XmlElement(name = "data-base64")
  private byte[] dataInBase64;

  public ForwardEwpApiFileResponseDto() {}

  public ForwardEwpApiFileResponseDto(String mediaType, byte[] dataInBase64) {
    this.mediaType = mediaType;
    this.dataInBase64 = dataInBase64;
  }

  public String getMediaType() {
    return mediaType;
  }

  public void setMediaType(String mediaType) {
    this.mediaType = mediaType;
  }

  public byte[] getDataDecoded() {
    return Base64.getDecoder().decode(this.dataInBase64);
  }

  public byte[] getDataInBase64() {
    return dataInBase64;
  }

  public void setDataInBase64(byte[] dataInBase64) {
    this.dataInBase64 = dataInBase64;
  }
}
