package pt.ulisboa.ewp.node.service.ewp.notification;

import static org.awaitility.Awaitility.await;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;

import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrResponseV1;
import java.time.Duration;
import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.UUID;
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
import pt.ulisboa.ewp.node.FeatureFlags;
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
import pt.ulisboa.ewp.node.service.ewp.notification.handler.EwpChangeNotificationHandlerCollection;
import pt.ulisboa.ewp.node.service.ewp.notification.handler.EwpOutgoingMobilityLearningAgreementChangeNotificationHandler;

@ContextConfiguration(classes = Config.class)
@ActiveProfiles(profiles = {"dev", "test",
    FeatureFlags.FEATURE_FLAG_WITH_SCHEDULERS}, inheritProfiles = false)
@TestPropertySource(properties = {
    "cnr.intervalInMilliseconds=1000",
    "cnr.maxNumberAttempts=3"
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
    public EwpChangeNotificationHandlerCollection changeNotificationHandlerCollection() {
      return new EwpChangeNotificationHandlerCollection(
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
  void testRun_ScheduledChangeNotificationSuccess_NotificationMarkedAsSuccess()
      throws EwpClientErrorException, NoEwpCnrAPIException {

    String sendingHeiId = UUID.randomUUID().toString();
    String receivingHeiId = UUID.randomUUID().toString();
    String omobilityId = UUID.randomUUID().toString();

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        1, ZonedDateTime.now(), Status.PENDING, sendingHeiId, receivingHeiId, omobilityId);

    EwpSuccessOperationResult<OmobilityLaCnrResponseV1> mockedSuccessResult = new EwpSuccessOperationResult.Builder<OmobilityLaCnrResponseV1>()
        .responseBody(new OmobilityLaCnrResponseV1())
        .build();

    doNothing().when(
        outgoingMobilityLearningAgreementChangeNotificationHandler).sendChangeNotification(
        originalChangeNotification);

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

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        cnrProperties.getMaxNumberAttempts(),
        ZonedDateTime.now(), Status.PENDING, sendingHeiId, receivingHeiId, omobilityId);

    doThrow(new NoEwpCnrAPIException(originalChangeNotification)).when(
        outgoingMobilityLearningAgreementChangeNotificationHandler).sendChangeNotification(
        originalChangeNotification);

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

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        cnrProperties.getMaxNumberAttempts(),
        ZonedDateTime.now(), Status.PENDING, sendingHeiId, receivingHeiId, omobilityId);

    doThrow(new EwpClientProcessorException(null, null, new IllegalStateException("TEST"))).when(
        outgoingMobilityLearningAgreementChangeNotificationHandler).sendChangeNotification(
        originalChangeNotification);

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

    EwpOutgoingMobilityLearningAgreementChangeNotification originalChangeNotification = new EwpOutgoingMobilityLearningAgreementChangeNotification(
        1, ZonedDateTime.now(), Status.PENDING, sendingHeiId, receivingHeiId, omobilityId);

    doThrow(new EwpClientProcessorException(null, null, new IllegalStateException("TEST"))).when(
        outgoingMobilityLearningAgreementChangeNotificationHandler).sendChangeNotification(
        originalChangeNotification);

    changeNotificationRepository.persist(originalChangeNotification);
    await()
        .atMost(
            Duration.ofMillis(cnrProperties.getIntervalInMilliseconds() + 1000))
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