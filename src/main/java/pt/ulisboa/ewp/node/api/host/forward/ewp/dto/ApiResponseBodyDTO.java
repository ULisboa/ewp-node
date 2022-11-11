package pt.ulisboa.ewp.node.api.host.forward.ewp.dto;

import java.util.Collection;
import java.util.Collections;

import pt.ulisboa.ewp.node.utils.messaging.Message;

public class ApiResponseBodyDTO<T> {

  private Collection<Message> messages;
  private T data;

  public ApiResponseBodyDTO() {
    this(Collections.emptyList(), null);
  }

  public ApiResponseBodyDTO(Collection<Message> messages, T data) {
    this.messages = messages;
    this.data = data;
  }

  public void setMessages(Collection<Message> messages) {
    this.messages = messages;
  }

  public Collection<Message> getMessages() {
    return messages;
  }

  public void setData(T data) {
    this.data = data;
  }

  public T getData() {
    return data;
  }
}
