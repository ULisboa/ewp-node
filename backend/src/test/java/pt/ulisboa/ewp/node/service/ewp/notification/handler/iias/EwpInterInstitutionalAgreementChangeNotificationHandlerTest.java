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
import pt.ulisboa.ewp.node.client.ewp.iias.cnr.EwpInterInstitutionalAgreementCnrV2Client;
import pt.ulisboa.ewp.node.client.ewp.iias.cnr.EwpInterInstitutionalAgreementCnrV3Client;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpInterInstitutionalAgreementChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.utils.EwpApi;

public class EwpInterInstitutionalAgreementChangeNotificationHandlerTest {

  private final EwpInterInstitutionalAgreementChangeNotificationHandler changeNotificationHandler;
  private final RegistryClient registryClient;
  private final EwpInterInstitutionalAgreementCnrV2Client interInstitutionalAgreementCnrV2Client;
  private final EwpInterInstitutionalAgreementCnrV3Client interInstitutionalAgreementCnrV3Client;

  public EwpInterInstitutionalAgreementChangeNotificationHandlerTest() {
    this.registryClient = Mockito.mock(RegistryClient.class);
    this.interInstitutionalAgreementCnrV2Client =
        mock(EwpInterInstitutionalAgreementCnrV2Client.class);
    this.interInstitutionalAgreementCnrV3Client =
        mock(EwpInterInstitutionalAgreementCnrV3Client.class);
    this.changeNotificationHandler =
        new EwpInterInstitutionalAgreementChangeNotificationHandler(
            this.registryClient,
            interInstitutionalAgreementCnrV2Client,
            interInstitutionalAgreementCnrV3Client);
  }

  @Test
  void testHandler_ScheduledNotificationAndCnrV3ApiSupported_NotificationSentViaV3Client()
      throws NoEwpCnrAPIException, EwpClientErrorException {
    try (MockedStatic<EwpApiUtils> ewpApiUtils = mockStatic(EwpApiUtils.class)) {
      // Given
      String partnerHeiId = "partner-hei-id";
      EwpInterInstitutionalAgreementChangeNotification changeNotification =
          new EwpInterInstitutionalAgreementChangeNotification(
              "sending-hei-id", partnerHeiId, "iia-id-test");
      ewpApiUtils
          .when(
              () ->
                  EwpApiUtils.getSupportedMajorVersions(
                      this.registryClient, partnerHeiId, EwpApi.INTERINSTITUTIONAL_AGREEMENT_CNR))
          .thenReturn(List.of(3));

      // When
      changeNotificationHandler.sendChangeNotification(changeNotification);

      // Then
      verify(this.interInstitutionalAgreementCnrV3Client, times(1))
          .sendChangeNotification(
              changeNotification.getPartnerHeiId(), List.of(changeNotification.getIiaId()));
      verify(this.interInstitutionalAgreementCnrV2Client, times(0))
          .sendChangeNotification(
              changeNotification.getNotifierHeiId(),
              changeNotification.getPartnerHeiId(),
              List.of(changeNotification.getIiaId()));
    }
  }

  @Test
  void testHandler_ScheduledNotificationAndCnrV2ApiSupported_NotificationSentViaV2Client()
      throws NoEwpCnrAPIException, EwpClientErrorException {
    try (MockedStatic<EwpApiUtils> ewpApiUtils = mockStatic(EwpApiUtils.class)) {
      // Given
      String partnerHeiId = "partner-hei-id";
      EwpInterInstitutionalAgreementChangeNotification changeNotification =
          new EwpInterInstitutionalAgreementChangeNotification(
              "sending-hei-id", partnerHeiId, "iia-id-test");
      ewpApiUtils
          .when(
              () ->
                  EwpApiUtils.getSupportedMajorVersions(
                      this.registryClient, partnerHeiId, EwpApi.INTERINSTITUTIONAL_AGREEMENT_CNR))
          .thenReturn(List.of(2));

      // When
      changeNotificationHandler.sendChangeNotification(changeNotification);

      // Then
      verify(this.interInstitutionalAgreementCnrV3Client, times(0))
          .sendChangeNotification(
              changeNotification.getPartnerHeiId(), List.of(changeNotification.getIiaId()));
      verify(this.interInstitutionalAgreementCnrV2Client, times(1))
          .sendChangeNotification(
              changeNotification.getNotifierHeiId(),
              changeNotification.getPartnerHeiId(),
              List.of(changeNotification.getIiaId()));
    }
  }
}
