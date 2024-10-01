package pt.ulisboa.ewp.node.api.host.forward.ewp.dto;

import jakarta.xml.bind.annotation.XmlAccessType;
import jakarta.xml.bind.annotation.XmlAccessorType;
import jakarta.xml.bind.annotation.XmlElement;
import jakarta.xml.bind.annotation.XmlElementWrapper;
import jakarta.xml.bind.annotation.XmlEnum;
import jakarta.xml.bind.annotation.XmlRootElement;
import jakarta.xml.bind.annotation.XmlSchemaType;
import jakarta.xml.bind.annotation.XmlType;
import java.util.ArrayList;
import java.util.List;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "")
@XmlRootElement(name = "forward-ewp-api-response")
public class ForwardEwpApiResponse {

  private Long communicationId;

  @XmlElementWrapper(name = "messages")
  @XmlElement(name = "message")
  private List<Message> messages;

  public ForwardEwpApiResponse() {
  }

  public ForwardEwpApiResponse(Long communicationId) {
    this.communicationId = communicationId;
  }

  public long getCommunicationId() {
    return communicationId;
  }

  public void setCommunicationId(long communicationId) {
    this.communicationId = communicationId;
  }

  public List<Message> getMessages() {
    if (this.messages == null) {
      this.messages = new ArrayList<>();
    }
    return this.messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  @XmlAccessorType(XmlAccessType.FIELD)
  @XmlType(
      name = "",
      propOrder = {"context", "severity", "summary"})
  public static class Message {

    @XmlElement(required = true)
    protected String context;

    @XmlElement(required = true)
    @XmlSchemaType(name = "string")
    protected MessageSeverity severity;

    @XmlElement(required = true)
    protected String summary;

    public String getContext() {
      return context;
    }

    public void setContext(String value) {
      this.context = value;
    }

    public MessageSeverity getSeverity() {
      return severity;
    }

    public void setSeverity(MessageSeverity value) {
      this.severity = value;
    }

    public String getSummary() {
      return summary;
    }

    public void setSummary(String value) {
      this.summary = value;
    }

    @XmlType(name = "message-severity")
    @XmlEnum
    public enum MessageSeverity {
      FATAL,
      ERROR,
      WARN,
      INFO;

      public String value() {
        return name();
      }

      public static MessageSeverity fromValue(String v) {
        return valueOf(v);
      }
    }
  }
}
