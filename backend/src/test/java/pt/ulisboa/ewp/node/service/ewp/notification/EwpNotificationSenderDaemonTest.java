package pt.ulisboa.ewp.node.service.ewp.notification;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrResponseV1;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientProcessorException;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.config.cnr.CnrProperties;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification.Status;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityLearningAgreementChangeNotification;
import pt.ulisboa.ewp.node.domain.repository.notification.EwpChangeNotificationRepository;
import pt.ulisboa.ewp.node.service.ewp.notification.EwpNotificationSenderDaemonTest.Config;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.service.ewp.notification.handler.omobilities.las.EwpOutgoingMobilityLearningAgreementChangeNotificationHandler;

@ContextConfiguration(classes = Config.class)
@ActiveProfiles(profiles = {"dev", "test"}, inheritProfiles = false)
@TestPropertySource(properties = {
    "cnr.intervalInMilliseconds=1000",
    "cnr.maxNumberAttempts=3",
    "scheduling.enabled=true"
})
class EwpNotificationSenderDaemonTest extends AbstractIntegrationTest {

  @Autowired
  private EwpChangeNotificationRepository changeNotificationRepository;

  @Autowired
  private EwpNotificationSenderDaemon notificationSenderDaemon;

  @Autowired
  private CnrProperties cnrProperties;

  @Autowired
  private EwpOutgoingMobilityLearningAgreementChangeNotificationHandler outgoingMobilityLearningAgreementChangeNotificationHandler;

  @Configuration
  public static class Config {

    @Bean
    @Primary
    public EwpNotificationSenderDaemon notificationSenderDaemon(CnrProperties cnrProperties,
                                                                   EwpChangeNotificationRepository changeNotificationRepository) {
      return new EwpNotificationSenderDaemon(cnrProperties, changeNotificationRepository,
              Collections.singletonList(outgoingMobilityLearningAgreementChangeNotificationHandler()));
    }

    @Bean
    @Primary
    public EwpOutgoingMobilityLearningAgreementChangeNotificationHandler outgoingMobilityLearningAgreementChangeNotificationHandler() {
      return Mockito.spy(
          new EwpOutgoingMobilityLearningAgreementChangeNotificationHandler(null, null));
    }
  }

  @Test
  void testRun_TwoThreadsSimultaneousSameChangeNotification_OnlyOneProcesses()
      throws InterruptedException, NoEwpCnrAPIException, EwpClientErrorException {
    EwpOutgoingMobilityLearningAgreementChangeNotification changeNotification =
        new EwpOutgoingMobilityLearningAgreementChangeNotification(
            null,
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString());
    changeNotificationRepository.deleteAll();
    changeNotificationRepository.persist(changeNotification);

    // NOTE: obtain the persisted change notification to simulate the pessimistic lock
    changeNotification =
        (EwpOutgoingMobilityLearningAgreementChangeNotification)
            changeNotificationRepository.findById(changeNotification.getId()).get();

    ExecutorService executorService = Executors.newFixedThreadPool(2);
    EwpOutgoingMobilityLearningAgreementChangeNotification[] changeNotifications = {
      changeNotification
    };
    executorService.submit(
        () -> {
          try {
            // NOTE: delay a little the start of one executor
            Thread.sleep(100L);
            notificationSenderDaemon.processChangeNotification(changeNotifications[0], true);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });
    executorService.submit(
        () -> {
          try {
            notificationSenderDaemon.processChangeNotification(changeNotifications[0], true);
          } catch (Exception e) {
            throw new RuntimeException(e);
          }
        });

    doAnswer(
            invocationOnMock -> {
              // NOTE: sleep for some time to give time to both threads attempt to execute the same
              // code.
              Thread.sleep(1000);
              return null;
            })
        .when(outgoingMobilityLearningAgreementChangeNotificationHandler)
        .sendChangeNotification(Mockito.any());

    // Wait for both tasks to complete
    executorService.shutdown();
    executorService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);

    await()
        .atMost(Duration.ofMillis(cnrProperties.getIntervalInMilliseconds() + 3000))
        .until(
            () ->
                changeNotificationRepository
                    .findById(changeNotifications[0].getId())
                    .get()
                    .wasSuccess());

    verify(outgoingMobilityLearningAgreementChangeNotificationHandler, times(1))
        .sendChangeNotification(Mockito.any());
  }

  @Test
  void testRun_ScheduledChangeNotificationSuccess_NotificationMarkedAsSuccess()
      throws EwpClientErrorException, NoEwpCnrAPIException {

    String sendingHeiId = UUID.randomUUID().toString();
    String receivingHeiId = UUID.randomUUID().toString();
    String omobilityId = UUID.randomUUID().toString();

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification =
        new EwpOutgoingMobilityLearningAgreementChangeNotification(
            null,
            1,
            ZonedDateTime.now(),
            Status.PENDING,
            sendingHeiId,
            receivingHeiId,
            omobilityId);

    EwpSuccessOperationResult<OmobilityLaCnrResponseV1> mockedSuccessResult = new EwpSuccessOperationResult.Builder<OmobilityLaCnrResponseV1>()
        .responseBody(new OmobilityLaCnrResponseV1())
        .build();

    doNothing()
        .when(outgoingMobilityLearningAgreementChangeNotificationHandler)
        .sendChangeNotification(Mockito.any());

    changeNotificationRepository.deleteAll();
    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(cnrProperties.getIntervalInMilliseconds() + 1000))
        .until(() -> changeNotificationRepository.findById(originalChangeNotification.getId()).get()
            .wasSuccess());
  }

  @Test
  void testRun_ScheduledChangeNotificationNoCnrApiAvailable_NotificationMarkedAsFailure()
      throws EwpClientErrorException, NoEwpCnrAPIException {

    String sendingHeiId = UUID.randomUUID().toString();
    String receivingHeiId = UUID.randomUUID().toString();
    String omobilityId = UUID.randomUUID().toString();

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification =
        new EwpOutgoingMobilityLearningAgreementChangeNotification(
            null,
            cnrProperties.getMaxNumberAttempts(),
            ZonedDateTime.now(),
            Status.PENDING,
            sendingHeiId,
            receivingHeiId,
            omobilityId);

    doThrow(new NoEwpCnrAPIException(originalChangeNotification))
        .when(outgoingMobilityLearningAgreementChangeNotificationHandler)
        .sendChangeNotification(Mockito.any());

    changeNotificationRepository.deleteAll();
    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(cnrProperties.getIntervalInMilliseconds() + 1000))
        .until(() -> changeNotificationRepository.findById(originalChangeNotification.getId()).get()
            .hasFailedDueToNoCnrApiAvailable());
  }

  @Test
  void testRun_ScheduledChangeNotificationLastAttemptFailure_NotificationMarkedAsFailure()
      throws EwpClientErrorException, NoEwpCnrAPIException {

    String sendingHeiId = UUID.randomUUID().toString();
    String receivingHeiId = UUID.randomUUID().toString();
    String omobilityId = UUID.randomUUID().toString();

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification =
        new EwpOutgoingMobilityLearningAgreementChangeNotification(
            null,
            cnrProperties.getMaxNumberAttempts(),
            ZonedDateTime.now(),
            Status.PENDING,
            sendingHeiId,
            receivingHeiId,
            omobilityId);

    doThrow(new EwpClientProcessorException(null, null, new IllegalStateException("TEST")))
        .when(outgoingMobilityLearningAgreementChangeNotificationHandler)
        .sendChangeNotification(Mockito.any());

    changeNotificationRepository.deleteAll();
    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(cnrProperties.getIntervalInMilliseconds() + 1000))
        .until(() -> changeNotificationRepository.findById(originalChangeNotification.getId()).get()
            .hasFailedDueToMaxAttempts());
  }

  @Test
  void testRun_ScheduledChangeNotificationNotLastAttemptFailure_NewAttemptScheduled()
      throws EwpClientErrorException, NoEwpCnrAPIException {

    String sendingHeiId = UUID.randomUUID().toString();
    String receivingHeiId = UUID.randomUUID().toString();
    String omobilityId = UUID.randomUUID().toString();

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification =
        new EwpOutgoingMobilityLearningAgreementChangeNotification(
            null,
            1,
            ZonedDateTime.now(),
            Status.PENDING,
            sendingHeiId,
            receivingHeiId,
            omobilityId);

    doThrow(new EwpClientProcessorException(null, null, new IllegalStateException("TEST")))
        .when(outgoingMobilityLearningAgreementChangeNotificationHandler)
        .sendChangeNotification(Mockito.any());

    changeNotificationRepository.deleteAll();
    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(Duration.ofMillis(cnrProperties.getIntervalInMilliseconds() + 1000))
        .until(
            () -> {
              EwpChangeNotification changeNotification =
                  changeNotificationRepository.findById(originalChangeNotification.getId()).get();
              return changeNotification.isPending()
                  && changeNotification.getAttemptNumber()
                      == originalChangeNotification.getAttemptNumber() + 1
                  && changeNotification
                      .getNextAttemptDateTime()
                      .isAfter(originalChangeNotification.getNextAttemptDateTime());
            });
  }
}