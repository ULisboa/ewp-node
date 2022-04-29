package pt.ulisboa.ewp.node.service.ewp.notification.handler;

import java.util.Collection;
import org.springframework.stereotype.Component;

/**
 * Class that stores change notification handlers.
 * This allows to easily pass specific handlers for testing.
 */
@Component
public class EwpChangeNotificationHandlerCollection {

  private final Collection<EwpChangeNotificationHandler> changeNotificationHandlers;

  public EwpChangeNotificationHandlerCollection(
      Collection<EwpChangeNotificationHandler> changeNotificationHandlers) {
    this.changeNotificationHandlers = changeNotificationHandlers;
  }

  public Collection<EwpChangeNotificationHandler> getChangeNotificationHandlers() {
    return changeNotificationHandlers;
  }
}
