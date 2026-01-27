package pt.ulisboa.ewp.node.service.ewp.notification.handler.iias;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.omobilities.cnr.EwpOutgoingMobilityCnrV1Client;
import pt.ulisboa.ewp.node.client.ewp.omobilities.cnr.EwpOutgoingMobilityCnrV2Client;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.service.ewp.notification.handler.omobilities.EwpOutgoingMobilityChangeNotificationHandler;
import pt.ulisboa.ewp.node.utils.EwpApi;

public class EwpOutgoingMobilityChangeNotificationHandlerTest {

  private final EwpOutgoingMobilityChangeNotificationHandler changeNotificationHandler;
  private final RegistryClient registryClient;
  private final EwpOutgoingMobilityCnrV1Client outgoingMobilityCnrV1Client;
  private final EwpOutgoingMobilityCnrV2Client outgoingMobilityCnrV2Client;

  public EwpOutgoingMobilityChangeNotificationHandlerTest() {
    this.registryClient = Mockito.mock(RegistryClient.class);
    this.outgoingMobilityCnrV1Client = mock(EwpOutgoingMobilityCnrV1Client.class);
    this.outgoingMobilityCnrV2Client = mock(EwpOutgoingMobilityCnrV2Client.class);
    this.changeNotificationHandler =
        new EwpOutgoingMobilityChangeNotificationHandler(
            this.registryClient,
            this.outgoingMobilityCnrV1Client,
            this.outgoingMobilityCnrV2Client);
  }

  @Test
  void testHandler_ScheduledNotificationAndCnrV2AndV1ApiSupported_NotificationSentViaV2Client()
      throws NoEwpCnrAPIException, EwpClientErrorException {
    try (MockedStatic<EwpApiUtils> ewpApiUtils = mockStatic(EwpApiUtils.class)) {
      // Given
      String partnerHeiId = "partner-hei-id";
      EwpOutgoingMobilityChangeNotification changeNotification =
          new EwpOutgoingMobilityChangeNotification(
              null, "sending-hei-id", partnerHeiId, "omobility-id-test");
      ewpApiUtils
          .when(
              () ->
                  EwpApiUtils.getSupportedMajorVersions(
                      this.registryClient, partnerHeiId, EwpApi.OUTGOING_MOBILITY_CNR))
          .thenReturn(List.of(1, 2));

      // When
      changeNotificationHandler.sendChangeNotification(changeNotification);

      // Then
      verify(this.outgoingMobilityCnrV2Client, times(1))
          .sendChangeNotification(
              changeNotification.getReceivingHeiId(),
              List.of(changeNotification.getOutgoingMobilityId()));
    }
  }

  @Test
  void testHandler_ScheduledNotificationAndCnrV2ApiSupported_NotificationSentViaV2Client()
      throws NoEwpCnrAPIException, EwpClientErrorException {
    try (MockedStatic<EwpApiUtils> ewpApiUtils = mockStatic(EwpApiUtils.class)) {
      // Given
      String partnerHeiId = "partner-hei-id";
      EwpOutgoingMobilityChangeNotification changeNotification =
          new EwpOutgoingMobilityChangeNotification(
              null, "sending-hei-id", partnerHeiId, "omobility-id-test");
      ewpApiUtils
          .when(
              () ->
                  EwpApiUtils.getSupportedMajorVersions(
                      this.registryClient, partnerHeiId, EwpApi.OUTGOING_MOBILITY_CNR))
          .thenReturn(List.of(2));

      // When
      changeNotificationHandler.sendChangeNotification(changeNotification);

      // Then
      verify(this.outgoingMobilityCnrV2Client, times(1))
          .sendChangeNotification(
              changeNotification.getReceivingHeiId(),
              List.of(changeNotification.getOutgoingMobilityId()));
    }
  }

  @Test
  void testHandler_ScheduledNotificationAndCnrV1ApiSupported_NotificationSentViaV1Client()
      throws NoEwpCnrAPIException, EwpClientErrorException {
    try (MockedStatic<EwpApiUtils> ewpApiUtils = mockStatic(EwpApiUtils.class)) {
      // Given
      String partnerHeiId = "partner-hei-id";
      EwpOutgoingMobilityChangeNotification changeNotification =
          new EwpOutgoingMobilityChangeNotification(
              null, "sending-hei-id", partnerHeiId, "omobility-id-test");
      ewpApiUtils
          .when(
              () ->
                  EwpApiUtils.getSupportedMajorVersions(
                      this.registryClient, partnerHeiId, EwpApi.OUTGOING_MOBILITY_CNR))
          .thenReturn(List.of(1));

      // When
      changeNotificationHandler.sendChangeNotification(changeNotification);

      // Then
      verify(this.outgoingMobilityCnrV1Client, times(1))
          .sendChangeNotification(
              changeNotification.getSendingHeiId(),
              changeNotification.getReceivingHeiId(),
              List.of(changeNotification.getOutgoingMobilityId()));
    }
  }
}
