package pt.ulisboa.ewp.node.service.messaging;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import pt.ulisboa.ewp.node.utils.messaging.Message;
import pt.ulisboa.ewp.node.utils.messaging.Severity;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

/** Represents a container of messages scoped to the current request. */
@Service
@Scope(WebApplicationContext.SCOPE_REQUEST)
public class MessageService {

  @Autowired private Logger logger;

  private Collection<Message> queue = new HashSet<>();

  public static MessageService getInstance() {
    return ApplicationContextProvider.getApplicationContext().getBean(MessageService.class);
  }

  public void add(String code) {
    add(Message.create(code));
  }

  public void add(Severity severity, String code) {
    add(Message.create(severity, code));
  }

  public void add(String context, Severity severity, String code) {
    add(Message.create(context, severity, code));
  }

  public void add(Message message) {
    if (Strings.isNotEmpty(message.getSummary())) this.queue.add(message);
    else logger.warn("Message discarded: empty summary");
  }

  public boolean hasMessages() {
    return !this.queue.isEmpty();
  }

  public boolean hasMessage(Message message) {
    return this.queue.contains(message);
  }

  public Collection<Message> consumeMessages() {
    Collection<Message> result = new LinkedList<>(queue);
    queue.clear();
    return result;
  }
}
