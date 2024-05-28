package pt.ulisboa.ewp.node.service.ewp.notification;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.config.cnr.CnrProperties;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;
import pt.ulisboa.ewp.node.service.communication.context.CommunicationContextHolder;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.service.ewp.notification.handler.EwpChangeNotificationHandler;

@Service
public class EwpNotificationSenderDaemon implements Runnable {

  private static final Logger LOG = LoggerFactory.getLogger(EwpNotificationSenderDaemon.class);

  private final CnrProperties cnrProperties;
  private final EwpChangeNotificationRepository changeNotificationRepository;

  private final Map<Class<?>, EwpChangeNotificationHandler> classTypeToSenderHandlerMap =
      new HashMap<>();

  public EwpNotificationSenderDaemon(
      CnrProperties cnrProperties,
      EwpChangeNotificationRepository changeNotificationRepository,
      Collection<EwpChangeNotificationHandler> changeNotificationHandlers) {
    this.cnrProperties = cnrProperties;
    this.changeNotificationRepository = changeNotificationRepository;

    this.setChangeNotificationHandlers(changeNotificationHandlers);
  }

  public void setChangeNotificationHandlers(
      Collection<EwpChangeNotificationHandler> changeNotificationHandlers) {
    this.setChangeNotificationHandlers(
        changeNotificationHandlers.toArray(new EwpChangeNotificationHandler[0]));
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
    Collection<EwpChangeNotification> changeNotifications =
        this.changeNotificationRepository.findAllPending();
    for (EwpChangeNotification changeNotification : changeNotifications) {
      try {
        processChangeNotification(changeNotification, false);
      } catch (Exception e) {
        LOG.error(
            String.format("Failed to process change notification: %s", changeNotification), e);
        throw new RuntimeException(e);
      }
    }
  }

  public void processChangeNotification(
      EwpChangeNotification ewpChangeNotification, boolean forceAttempt) throws Exception {
    if (!forceAttempt) {
      if (!ewpChangeNotification.isPending()) {
        return;
      }

      if (ZonedDateTime.now().isBefore(ewpChangeNotification.getNextAttemptDateTime())) {
        return;
      }
    }

    CommunicationContextHolder.runInNestedContext(
        context -> {
          context.setCurrentEwpChangeNotifications(List.of(ewpChangeNotification));

          try {
            sendChangeNotification(ewpChangeNotification);

            ewpChangeNotification.markAsSuccess();
            changeNotificationRepository.persist(ewpChangeNotification);

          } catch (NoEwpCnrAPIException e) {
            LOG.error(
                String.format(
                    "Discarding change notification due to no CNR API available: %s",
                    ewpChangeNotification),
                e);
            ewpChangeNotification.markAsFailedDueToNoCnrApiAvailable();
            changeNotificationRepository.persist(ewpChangeNotification);

          } catch (Exception e) {
            LOG.error(
                String.format("Failed to send change notification: %s", ewpChangeNotification), e);
            scheduleNewAttempt(ewpChangeNotification);
          }

          return null;
        });
  }

  private void sendChangeNotification(EwpChangeNotification changeNotification)
      throws EwpClientErrorException, NoEwpCnrAPIException {

    Optional<EwpChangeNotificationHandler> senderHandlerOptional =
        this.getSenderHandlerForClassType(changeNotification.getClass());
    if (senderHandlerOptional.isPresent()) {
      senderHandlerOptional.get().sendChangeNotification(changeNotification);
    } else {
      throw new IllegalStateException(
          "Unsupported change notification type: " + changeNotification);
    }
  }

  private void scheduleNewAttempt(EwpChangeNotification changeNotification) {
    if (changeNotification.getAttemptNumber() >= cnrProperties.getMaxNumberAttempts()) {
      changeNotification.markAsFailedDueToMaxAttempts();

    } else {
      changeNotification.scheduleNewAttempt();
    }

    changeNotificationRepository.persist(changeNotification);
  }

  private <T extends EwpChangeNotification>
      Optional<EwpChangeNotificationHandler> getSenderHandlerForClassType(Class<T> classType) {
    return Optional.ofNullable(this.classTypeToSenderHandlerMap.get(classType));
  }

  private void registerSenderHandler(Class<?> classType, EwpChangeNotificationHandler sendHandler) {
    this.classTypeToSenderHandlerMap.put(classType, sendHandler);
  }

  public Date getNextExecutionTime(TriggerContext context) {
    Optional<Date> lastCompletionTime = Optional.ofNullable(context.lastCompletionTime());
    Instant nextExecutionTime =
        lastCompletionTime
            .orElseGet(Date::new)
            .toInstant()
            .plusMillis(this.cnrProperties.getIntervalInMilliseconds());
    return Date.from(nextExecutionTime);
  }
}
