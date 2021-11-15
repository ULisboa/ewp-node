package pt.ulisboa.ewp.node.service.ewp.notification.handler;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.iias.cnr.EwpInterInstitutionalAgreementCnrV2Client;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpInterInstitutionalAgreementChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.utils.EwpApi;

@Service
public class EwpInterInstitutionalAgreementChangeNotificationHandler extends
    EwpChangeNotificationHandler {

  private final EwpInterInstitutionalAgreementCnrV2Client interInstitutionalAgreementCnrV2Client;

  public EwpInterInstitutionalAgreementChangeNotificationHandler(
      RegistryClient registryClient,
      EwpInterInstitutionalAgreementCnrV2Client interInstitutionalAgreementCnrV2Client) {
    super(registryClient);
    this.interInstitutionalAgreementCnrV2Client = interInstitutionalAgreementCnrV2Client;
  }

  @Override
  public Class<?> getSupportedChangeNotificationClassType() {
    return EwpInterInstitutionalAgreementChangeNotification.class;
  }

  @Override
  public void sendChangeNotification(
      EwpChangeNotification changeNotification)
      throws EwpClientErrorException, NoEwpCnrAPIException {

    if (!(changeNotification instanceof EwpInterInstitutionalAgreementChangeNotification)) {
      throw new IllegalArgumentException("Invalid change notification type: " + changeNotification);
    }

    EwpInterInstitutionalAgreementChangeNotification interInstitutionalAgreementChangeNotification = (EwpInterInstitutionalAgreementChangeNotification) changeNotification;

    String targetHeiId = interInstitutionalAgreementChangeNotification.getPartnerHeiId();
    List<Integer> supportedMajorVersions = EwpApiUtils.getSupportedMajorVersions(
        getRegistryClient(), targetHeiId, EwpApi.INTERINSTITUTIONAL_AGREEMENT_CNR);

    if (supportedMajorVersions.contains(2)) {
      sendChangeNotificationVersion2(interInstitutionalAgreementChangeNotification);
    } else {
      throw new NoEwpCnrAPIException(changeNotification);
    }
  }

  private void sendChangeNotificationVersion2(
      EwpInterInstitutionalAgreementChangeNotification changeNotification)
      throws EwpClientErrorException {

    interInstitutionalAgreementCnrV2Client.sendChangeNotification(
        changeNotification.getNotifierHeiId(),
        changeNotification.getPartnerHeiId(),
        Collections.singletonList(changeNotification.getIiaId()));
  }
}
