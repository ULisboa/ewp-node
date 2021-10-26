package pt.ulisboa.ewp.node.service.ewp.notification;

import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.omobilities.EwpOutgoingMobilityCnrV1Client;
import pt.ulisboa.ewp.node.client.ewp.omobilities.las.cnr.EwpOutgoingMobilityLearningAgreementCnrV1Client;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityLearningAgreementChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;

@Service
public class EwpNotificationSenderDaemon implements Runnable {

  // TODO allow to set this by setting
  public static final int TASK_INTERVAL_IN_MILLISECONDS = 5000;

  // TODO allow to set this by setting
  public static final int MAX_NUMBER_ATTEMPTS = 10; // maximum wait time = 2^10 minutes = 1024 minutes ~ 17 hours

  private static final Logger LOG = LoggerFactory.getLogger(EwpNotificationSenderDaemon.class);

  private final EwpChangeNotificationRepository changeNotificationRepository;

  private final EwpOutgoingMobilityCnrV1Client outgoingMobilityCnrV1Client;
  private final EwpOutgoingMobilityLearningAgreementCnrV1Client outgoingMobilityLearningAgreementCnrV1Client;

  public EwpNotificationSenderDaemon(
      EwpChangeNotificationRepository changeNotificationRepository,
      EwpOutgoingMobilityCnrV1Client outgoingMobilityCnrV1Client,
      EwpOutgoingMobilityLearningAgreementCnrV1Client outgoingMobilityLearningAgreementCnrV1Client) {
    this.changeNotificationRepository = changeNotificationRepository;
    this.outgoingMobilityCnrV1Client = outgoingMobilityCnrV1Client;
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
    try {
      sendChangeNotification(changeNotification);

      changeNotification.markAsSuccess();
      changeNotificationRepository.persist(changeNotification);

    } catch (Exception e) {
      LOG.error(String.format("Failed to send change notification: %s", changeNotification), e);
      scheduleNewAttempt(changeNotification);
    }
  }

  private void sendChangeNotification(
      EwpChangeNotification changeNotification) throws EwpClientErrorException {

    if (changeNotification instanceof EwpOutgoingMobilityChangeNotification) {
      EwpOutgoingMobilityChangeNotification outgoingMobilityChangeNotification = (EwpOutgoingMobilityChangeNotification) changeNotification;
      outgoingMobilityCnrV1Client.sendChangeNotification(
          outgoingMobilityChangeNotification.getSendingHeiId(),
          outgoingMobilityChangeNotification.getReceivingHeiId(),
          Collections.singletonList(outgoingMobilityChangeNotification.getOutgoingMobilityId()));

    } else if (changeNotification instanceof EwpOutgoingMobilityLearningAgreementChangeNotification) {
      EwpOutgoingMobilityLearningAgreementChangeNotification outgoingMobilityLearningAgreementChangeNotification = (EwpOutgoingMobilityLearningAgreementChangeNotification) changeNotification;
      outgoingMobilityLearningAgreementCnrV1Client.sendChangeNotification(
          outgoingMobilityLearningAgreementChangeNotification.getSendingHeiId(),
          outgoingMobilityLearningAgreementChangeNotification.getReceivingHeiId(),
          Collections.singletonList(
              outgoingMobilityLearningAgreementChangeNotification.getOutgoingMobilityId()));

    } else {
      throw new IllegalStateException(
          "Unsupported change notification type: " + changeNotification);
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
