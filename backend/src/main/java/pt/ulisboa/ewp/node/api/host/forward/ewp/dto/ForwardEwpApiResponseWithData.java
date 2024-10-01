package pt.ulisboa.ewp.node.api.host.forward.ewp.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlAnyElement;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "forward-ewp-api-response-with-data")
public class ForwardEwpApiResponseWithData<T> extends ForwardEwpApiResponse {

  private Data<T> data;

  public ForwardEwpApiResponseWithData() {
  }

  public ForwardEwpApiResponseWithData(Long communicationId) {
    super(communicationId);
  }

  public Data<T> getData() {
    if (this.data == null) {
      this.data = new Data<>();
    }
    return this.data;
  }

  public void setData(Data<T> data) {
    this.data = data;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(
      name = "",
      propOrder = {"object"})
  public static class Data<T> {

    @XmlAnyElement(lax = true)
    private T object;

    public T getObject() {
      return object;
    }

    public void setObject(T object) {
      this.object = object;
    }
  }
}
