package pt.ulisboa.ewp.node.utils.messaging;

import java.io.Serializable;
import java.util.Objects;

import com.fasterxml.jackson.annotation.JsonInclude;

public class Message implements Serializable {

  @JsonInclude(JsonInclude.Include.NON_NULL)
  private String context;

  private Severity severity;
  private String summary;

  private Message(String context, Severity severity, String summary) {
    this.context = context;
    this.severity = severity;
    this.summary = summary;
  }

  /** Creates a new message with severity INFO. */
  public static Message create(String summary) {
    return create(Severity.INFO, summary);
  }

  public static Message create(Severity severity, String summary) {
    return create(null, severity, summary);
  }

  public static Message create(String context, Severity severity, String summary) {
    return new Message(context, severity, summary);
  }

  public String getContext() {
    return context;
  }

  public Severity getSeverity() {
    return severity;
  }

  public String getSummary() {
    return summary;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    Message message = (Message) o;
    return Objects.equals(context, message.context)
        && severity == message.severity
        && Objects.equals(summary, message.summary);
  }

  @Override
  public int hashCode() {
    return Objects.hash(context, severity, summary);
  }

  @Override
  public String toString() {
    return "Message(provider = "
        + context
        + ", severity = "
        + severity
        + ", summary = "
        + summary
        + ")";
  }

  public static class Builder {

    private String context;
    private Severity severity;
    private String summary;

    public Builder(String summary) {
      this(Severity.INFO, summary);
    }

    public Builder(Severity severity, String summary) {
      this.severity = severity;
      this.summary = summary;
    }

    public Builder context(String context) {
      this.context = context;
      return this;
    }

    public Message build() {
      return new Message(context, severity, summary);
    }
  }
}
