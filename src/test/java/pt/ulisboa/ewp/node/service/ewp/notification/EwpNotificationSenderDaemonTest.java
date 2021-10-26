package pt.ulisboa.ewp.node.service.ewp.notification;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.when;

import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrResponseV1;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.omobilities.las.cnr.EwpOutgoingMobilityLearningAgreementCnrV1Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification.Status;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityLearningAgreementChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;

class EwpNotificationSenderDaemonTest extends AbstractIntegrationTest {

  @Autowired
  private EwpChangeNotificationRepository changeNotificationRepository;

  @MockBean
  private EwpOutgoingMobilityLearningAgreementCnrV1Client outgoingMobilityLearningAgreementCnrV1Client;

  @Test
  void testRun_ScheduledChangeNotificationSuccess_NotificationMarkedAsSuccess()
      throws EwpClientErrorException {

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        1, ZonedDateTime.now(), Status.PENDING, "abc", "qwe", "def");

    EwpSuccessOperationResult<OmobilityLaCnrResponseV1> mockedSuccessResult = new EwpSuccessOperationResult.Builder<OmobilityLaCnrResponseV1>()
        .responseBody(new OmobilityLaCnrResponseV1())
        .build();

    when(outgoingMobilityLearningAgreementCnrV1Client.sendChangeNotification(
        originalChangeNotification.getSendingHeiId(),
        originalChangeNotification.getReceivingHeiId(),
        Collections.singletonList(originalChangeNotification.getOutgoingMobilityId())))
        .thenReturn(mockedSuccessResult);

    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(EwpNotificationSenderDaemon.TASK_INTERVAL_IN_MILLISECONDS + 2000))
        .until(() -> changeNotificationRepository.findById(originalChangeNotification.getId()).get()
            .wasSuccess());
  }

  @Test
  void testRun_ScheduledChangeNotificationLastAttemptFailure_NotificationMarkedAsFailure()
      throws EwpClientErrorException {

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        EwpNotificationSenderDaemon.MAX_NUMBER_ATTEMPTS,
        ZonedDateTime.now(), Status.PENDING, "abc", "qwe", "def");

    when(outgoingMobilityLearningAgreementCnrV1Client.sendChangeNotification(
        originalChangeNotification.getSendingHeiId(),
        originalChangeNotification.getReceivingHeiId(),
        Collections.singletonList(originalChangeNotification.getOutgoingMobilityId()))).thenThrow(
        new EwpClientProcessorException(null, null, new IllegalStateException("TEST")));

    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(EwpNotificationSenderDaemon.TASK_INTERVAL_IN_MILLISECONDS + 2000))
        .until(() -> changeNotificationRepository.findById(originalChangeNotification.getId()).get()
            .hasFailed());
  }

  @Test
  void testRun_ScheduledChangeNotificationNotLastAttemptFailure_NewAttemptScheduled()
      throws EwpClientErrorException {

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        1, ZonedDateTime.now(), Status.PENDING, "abc", "qwe", "def");

    when(outgoingMobilityLearningAgreementCnrV1Client.sendChangeNotification(
        originalChangeNotification.getSendingHeiId(),
        originalChangeNotification.getReceivingHeiId(),
        Collections.singletonList(originalChangeNotification.getOutgoingMobilityId()))).thenThrow(
        new EwpClientProcessorException(null, null, new IllegalStateException("TEST")));

    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(EwpNotificationSenderDaemon.TASK_INTERVAL_IN_MILLISECONDS + 2000))
        .until(() -> {
          EwpChangeNotification changeNotification = changeNotificationRepository.findById(
              originalChangeNotification.getId()).get();
          return changeNotification.isPending() && changeNotification.getAttemptNumber()
              == originalChangeNotification.getAttemptNumber() + 1 &&
              changeNotification.getScheduledDateTime()
                  .isAfter(originalChangeNotification.getScheduledDateTime());
        });
  }
}