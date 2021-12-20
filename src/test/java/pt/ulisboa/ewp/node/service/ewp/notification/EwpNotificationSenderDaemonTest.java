package pt.ulisboa.ewp.node.service.ewp.notification;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrResponseV1;
import java.time.Duration;
import java.time.ZonedDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification.Status;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityLearningAgreementChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.service.ewp.notification.handler.EwpOutgoingMobilityLearningAgreementChangeNotificationHandler;

class EwpNotificationSenderDaemonTest extends AbstractIntegrationTest {

  @Autowired
  private EwpChangeNotificationRepository changeNotificationRepository;

  @SpyBean
  private EwpOutgoingMobilityLearningAgreementChangeNotificationHandler outgoingMobilityLearningAgreementChangeNotificationHandler;

  @Test
  void testRun_ScheduledChangeNotificationSuccess_NotificationMarkedAsSuccess()
      throws EwpClientErrorException, NoEwpCnrAPIException {

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        1, ZonedDateTime.now(), Status.PENDING, "abc", "qwe", "def");

    EwpSuccessOperationResult<OmobilityLaCnrResponseV1> mockedSuccessResult = new EwpSuccessOperationResult.Builder<OmobilityLaCnrResponseV1>()
        .responseBody(new OmobilityLaCnrResponseV1())
        .build();

    doNothing().when(
        outgoingMobilityLearningAgreementChangeNotificationHandler).sendChangeNotification(
        Mockito.any());

    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(EwpNotificationSenderDaemon.TASK_INTERVAL_IN_MILLISECONDS + 5000))
        .until(() -> changeNotificationRepository.findById(originalChangeNotification.getId()).get()
            .wasSuccess());
  }

  @Test
  void testRun_ScheduledChangeNotificationNoCnrApiAvailable_NotificationMarkedAsFailure()
      throws EwpClientErrorException, NoEwpCnrAPIException {

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        EwpNotificationSenderDaemon.MAX_NUMBER_ATTEMPTS,
        ZonedDateTime.now(), Status.PENDING, "abc", "qwe", "def");

    doThrow(new NoEwpCnrAPIException(originalChangeNotification)).when(
        outgoingMobilityLearningAgreementChangeNotificationHandler).sendChangeNotification(
        Mockito.any());

    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(EwpNotificationSenderDaemon.TASK_INTERVAL_IN_MILLISECONDS + 5000))
        .until(() -> changeNotificationRepository.findById(originalChangeNotification.getId()).get()
            .hasFailedDueToNoCnrApiAvailable());
  }

  @Test
  void testRun_ScheduledChangeNotificationLastAttemptFailure_NotificationMarkedAsFailure()
      throws EwpClientErrorException, NoEwpCnrAPIException {

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        EwpNotificationSenderDaemon.MAX_NUMBER_ATTEMPTS,
        ZonedDateTime.now(), Status.PENDING, "abc", "qwe", "def");

    doThrow(new EwpClientProcessorException(null, null, new IllegalStateException("TEST"))).when(
        outgoingMobilityLearningAgreementChangeNotificationHandler).sendChangeNotification(
        Mockito.any());

    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(EwpNotificationSenderDaemon.TASK_INTERVAL_IN_MILLISECONDS + 5000))
        .until(() -> changeNotificationRepository.findById(originalChangeNotification.getId()).get()
            .hasFailedDueToMaxAttempts());
  }

  @Test
  void testRun_ScheduledChangeNotificationNotLastAttemptFailure_NewAttemptScheduled()
      throws EwpClientErrorException, NoEwpCnrAPIException {

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        1, ZonedDateTime.now(), Status.PENDING, "abc", "qwe", "def");

    doThrow(new EwpClientProcessorException(null, null, new IllegalStateException("TEST"))).when(
        outgoingMobilityLearningAgreementChangeNotificationHandler).sendChangeNotification(
        Mockito.any());

    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(EwpNotificationSenderDaemon.TASK_INTERVAL_IN_MILLISECONDS + 5000))
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