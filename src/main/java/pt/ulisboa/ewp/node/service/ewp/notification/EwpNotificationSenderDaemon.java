package pt.ulisboa.ewp.node.service.ewp.notification;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.service.ewp.notification.handler.EwpChangeNotificationHandler;
import pt.ulisboa.ewp.node.service.ewp.notification.handler.EwpChangeNotificationHandlerCollection;

@Service
public class EwpNotificationSenderDaemon implements Runnable {

  // TODO allow to set this by setting
  public static final int TASK_INTERVAL_IN_MILLISECONDS = 5000;

  // TODO allow to set this by setting
  public static final int MAX_NUMBER_ATTEMPTS = 10; // maximum wait time = 2^10 minutes = 1024 minutes ~ 17 hours

  private static final Logger LOG = LoggerFactory.getLogger(EwpNotificationSenderDaemon.class);

  private final EwpChangeNotificationRepository changeNotificationRepository;

  private final Map<Class<?>, EwpChangeNotificationHandler> classTypeToSenderHandlerMap = new HashMap<>();

  public EwpNotificationSenderDaemon(
      EwpChangeNotificationRepository changeNotificationRepository,
      EwpChangeNotificationHandlerCollection changeNotificationHandlerCollection) {
    this.changeNotificationRepository = changeNotificationRepository;

    this.setChangeNotificationHandlers(
        changeNotificationHandlerCollection.getChangeNotificationHandlers()
            .toArray(new EwpChangeNotificationHandler[0]));
  }

  public void setChangeNotificationHandlers(
      EwpChangeNotificationHandler... changeNotificationHandlers) {
    this.classTypeToSenderHandlerMap.clear();
    for (EwpChangeNotificationHandler changeNotificationHandler : changeNotificationHandlers) {
      this.registerSenderHandler(
          changeNotificationHandler.getSupportedChangeNotificationClassType(),
          changeNotificationHandler);
    }
  }

  @Override
  public void run() {
    Collection<EwpChangeNotification> changeNotifications = this.changeNotificationRepository.findAllPending();
    for (EwpChangeNotification changeNotification : changeNotifications) {
      if (ZonedDateTime.now().isAfter(changeNotification.getScheduledDateTime())) {
        processChangeNotification(changeNotification);
      }
    }
  }

  private void processChangeNotification(EwpChangeNotification changeNotification) {
    try {
      sendChangeNotification(changeNotification);

      changeNotification.markAsSuccess();
      changeNotificationRepository.persist(changeNotification);

    } catch (NoEwpCnrAPIException e) {
      LOG.error(String.format("Discarding change notification due to no CNR API available: %s",
          changeNotification), e);
      changeNotification.markAsFailedDueToNoCnrApiAvailable();
      changeNotificationRepository.persist(changeNotification);

    } catch (Exception e) {
      LOG.error(String.format("Failed to send change notification: %s", changeNotification), e);
      scheduleNewAttempt(changeNotification);
    }
  }

  private void sendChangeNotification(
      EwpChangeNotification changeNotification)
      throws EwpClientErrorException, NoEwpCnrAPIException {

    Optional<EwpChangeNotificationHandler> senderHandlerOptional = this.getSenderHandlerForClassType(
        changeNotification.getClass());
    if (senderHandlerOptional.isPresent()) {
      senderHandlerOptional.get().sendChangeNotification(changeNotification);
    } else {
      throw new IllegalStateException(
          "Unsupported change notification type: " + changeNotification);
    }
  }

  private void scheduleNewAttempt(EwpChangeNotification changeNotification) {
    if (changeNotification.getAttemptNumber() >= MAX_NUMBER_ATTEMPTS) {
      changeNotification.markAsFailedDueToMaxAttempts();

    } else {
      changeNotification.scheduleNewAttempt();
    }

    changeNotificationRepository.persist(changeNotification);
  }

  private <T extends EwpChangeNotification> Optional<EwpChangeNotificationHandler> getSenderHandlerForClassType(
      Class<T> classType) {
    return Optional.ofNullable(this.classTypeToSenderHandlerMap.get(classType));
  }

  private void registerSenderHandler(Class<?> classType,
      EwpChangeNotificationHandler sendHandler) {
    this.classTypeToSenderHandlerMap.put(classType, sendHandler);
  }
}
