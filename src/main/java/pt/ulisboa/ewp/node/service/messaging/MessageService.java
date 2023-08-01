package pt.ulisboa.ewp.node.service.messaging;

import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.ewp.node.utils.messaging.Message;
import pt.ulisboa.ewp.node.utils.messaging.Severity;

/** Represents a container of messages scoped to the current thread. */
public class MessageService {

  private static final Logger LOG = LoggerFactory.getLogger(MessageService.class);

  private static final ThreadLocal<MessageService> instanceHolder = new ThreadLocal<>();

  private final Collection<Message> queue = new HashSet<>();

  public static MessageService getInstance() {
    MessageService instance = instanceHolder.get();
    if (instance == null) {
      instance = new MessageService();
      instanceHolder.set(instance);
    }
    return instance;
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
    if (Strings.isNotEmpty(message.getSummary())) {
      this.queue.add(message);
    }
    else {
      LOG.warn("Message discarded: empty summary");
    }
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
