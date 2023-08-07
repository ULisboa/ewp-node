package pt.ulisboa.ewp.node.client.ewp.operation.request.body;

import java.io.Serializable;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

public class EwpRequestSerializableBody extends EwpRequestBody {

  private final Serializable serializable;

  public EwpRequestSerializableBody(Serializable serializable) {
    this.serializable = serializable;
  }

  public Serializable getSerializable() {
    return serializable;
  }

  @Override
  public String serialize() {
    return XmlUtils.marshall(serializable);
  }
}
