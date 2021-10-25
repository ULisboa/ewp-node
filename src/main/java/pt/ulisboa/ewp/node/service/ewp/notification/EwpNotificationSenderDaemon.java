package pt.ulisboa.ewp.node.service.ewp.notification;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.omobilities.las.cnr.EwpOutgoingMobilityLearningAgreementCnrV1Client;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;

@Service
public class EwpNotificationSenderDaemon implements Runnable {

  // TODO allow to set this by setting
  public static final int TASK_INTERVAL_IN_MILLISECONDS = 5000;

  // TODO allow to set this by setting
  public static final int MAX_NUMBER_ATTEMPTS = 10; // maximum wait time = 2^10 minutes = 1024 minutes ~ 17 hours

  private static final Logger LOG = LoggerFactory.getLogger(EwpNotificationSenderDaemon.class);

  private final EwpChangeNotificationRepository changeNotificationRepository;

  private final EwpOutgoingMobilityLearningAgreementCnrV1Client outgoingMobilityLearningAgreementCnrV1Client;

  public EwpNotificationSenderDaemon(
      EwpChangeNotificationRepository changeNotificationRepository,
      EwpOutgoingMobilityLearningAgreementCnrV1Client outgoingMobilityLearningAgreementCnrV1Client) {
    this.changeNotificationRepository = changeNotificationRepository;
    this.outgoingMobilityLearningAgreementCnrV1Client = outgoingMobilityLearningAgreementCnrV1Client;
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
    if (changeNotification instanceof EwpOutgoingMobilityChangeNotification) {
      processOutgoingMobilityChangeNotification(
          (EwpOutgoingMobilityChangeNotification) changeNotification);
    }
  }

  private void processOutgoingMobilityChangeNotification(
      EwpOutgoingMobilityChangeNotification changeNotification) {
    try {
      outgoingMobilityLearningAgreementCnrV1Client.sendChangeNotification(
          changeNotification.getSendingHeiId(),
          Collections.singletonList(changeNotification.getOutgoingMobilityId()));

      changeNotification.markAsSuccess();
      changeNotificationRepository.persist(changeNotification);

    } catch (EwpClientErrorException e) {
      LOG.error(String.format("Failed to send change notification: %s", changeNotification), e);
      scheduleNewAttempt(changeNotification);
    }
  }

  private void scheduleNewAttempt(EwpChangeNotification changeNotification) {
    if (changeNotification.getAttemptNumber() >= MAX_NUMBER_ATTEMPTS) {
      changeNotification.markAsFailed();

    } else {
      changeNotification.scheduleNewAttempt();
    }

    changeNotificationRepository.persist(changeNotification);
  }
}
