package pt.ulisboa.ewp.node.api.admin.dto.response;

import java.util.ArrayList;
import java.util.List;

public class AdminApiResponseDto {

  private List<Message> messages;

  public AdminApiResponseDto() {
    this(new ArrayList<>());
  }

  public AdminApiResponseDto(List<Message> messages) {
    this.messages = messages;
  }

  public List<Message> getMessages() {
    return messages;
  }

  public void setMessages(List<Message> messages) {
    this.messages = messages;
  }

  public static class Message {

    protected String context;
    protected MessageSeverity severity;
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

    public enum MessageSeverity {
      FATAL,
      ERROR,
      WARN,
      INFO;

      public static MessageSeverity fromValue(String v) {
        return valueOf(v);
      }

      public String value() {
        return name();
      }
    }
  }
}
