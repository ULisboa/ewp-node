package pt.ulisboa.ewp.node.domain.repository.notification;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

import java.time.ZonedDateTime;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import pt.ulisboa.ewp.node.AbstractIntegrationTest;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification.Status;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityChangeNotification;

class EwpChangeNotificationRepositoryTest extends AbstractIntegrationTest {

  @Autowired
  private EwpChangeNotificationRepository changeNotificationRepository;

  @Test
  public void testPersist_OldNotificationIsNotMergeableIntoNewNotification_BothNotificationsMaintainStatus() {
    EwpOutgoingMobilityChangeNotification oldChangeNotification =
        new EwpOutgoingMobilityChangeNotification(
            null,
            1,
            ZonedDateTime.now(),
            Status.PENDING,
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString());
    changeNotificationRepository.persist(oldChangeNotification);

    EwpOutgoingMobilityChangeNotification newChangeNotification =
        new EwpOutgoingMobilityChangeNotification(
            null,
            1,
            ZonedDateTime.now(),
            Status.PENDING,
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString());
    changeNotificationRepository.persist(newChangeNotification);

    Optional<EwpChangeNotification> updatedOldChangeNotificationOptional = changeNotificationRepository.findById(
        oldChangeNotification.getId());
    assertThat(updatedOldChangeNotificationOptional).isNotEmpty();
    assertThat(updatedOldChangeNotificationOptional.get().isPending()).isTrue();

    Optional<EwpChangeNotification> updatedNewChangeNotificationOptional = changeNotificationRepository.findById(
        newChangeNotification.getId());
    assertThat(updatedNewChangeNotificationOptional).isNotEmpty();
    assertThat(updatedNewChangeNotificationOptional.get().isPending()).isTrue();
  }

  @Test
  public void testPersist_OldNotificationIsMergeableIntoNewNotification_OldNotificationIsMerged() {
    EwpOutgoingMobilityChangeNotification oldChangeNotification =
        new EwpOutgoingMobilityChangeNotification(
            null,
            1,
            ZonedDateTime.now(),
            Status.PENDING,
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString(),
            UUID.randomUUID().toString());
    changeNotificationRepository.persist(oldChangeNotification);

    EwpOutgoingMobilityChangeNotification newChangeNotification =
        new EwpOutgoingMobilityChangeNotification(
            null,
            1,
            ZonedDateTime.now(),
            Status.PENDING,
            oldChangeNotification.getSendingHeiId(),
            oldChangeNotification.getReceivingHeiId(),
            oldChangeNotification.getOutgoingMobilityId());
    changeNotificationRepository.persist(newChangeNotification);

    Optional<EwpChangeNotification> updatedOldChangeNotificationOptional = changeNotificationRepository.findById(
        oldChangeNotification.getId());
    assertThat(updatedOldChangeNotificationOptional).isNotEmpty();
    assertThat(updatedOldChangeNotificationOptional.get().wasMerged()).isTrue();

    Optional<EwpChangeNotification> updatedNewChangeNotificationOptional = changeNotificationRepository.findById(
        newChangeNotification.getId());
    assertThat(updatedNewChangeNotificationOptional).isNotEmpty();
    assertThat(updatedNewChangeNotificationOptional.get().isPending()).isTrue();
  }

}