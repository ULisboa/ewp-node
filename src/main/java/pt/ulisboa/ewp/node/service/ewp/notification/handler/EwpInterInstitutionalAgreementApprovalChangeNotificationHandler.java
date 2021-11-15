package pt.ulisboa.ewp.node.service.ewp.notification.handler;

import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.iias.approval.cnr.EwpInterInstitutionalAgreementApprovalCnrV1Client;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpInterInstitutionalAgreementApprovalChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.utils.EwpApi;

@Service
public class EwpInterInstitutionalAgreementApprovalChangeNotificationHandler extends
    EwpChangeNotificationHandler {

  private final EwpInterInstitutionalAgreementApprovalCnrV1Client interInstitutionalAgreementApprovalCnrV1Client;

  public EwpInterInstitutionalAgreementApprovalChangeNotificationHandler(
      RegistryClient registryClient,
      EwpInterInstitutionalAgreementApprovalCnrV1Client interInstitutionalAgreementApprovalCnrV1Client) {
    super(registryClient);
    this.interInstitutionalAgreementApprovalCnrV1Client = interInstitutionalAgreementApprovalCnrV1Client;
  }

  @Override
  public Class<?> getSupportedChangeNotificationClassType() {
    return EwpInterInstitutionalAgreementApprovalChangeNotification.class;
  }

  @Override
  public void sendChangeNotification(
      EwpChangeNotification changeNotification)
      throws EwpClientErrorException, NoEwpCnrAPIException {

    if (!(changeNotification instanceof EwpInterInstitutionalAgreementApprovalChangeNotification)) {
      throw new IllegalArgumentException("Invalid change notification type: " + changeNotification);
    }

    EwpInterInstitutionalAgreementApprovalChangeNotification interInstitutionalAgreementApprovalChangeNotification = (EwpInterInstitutionalAgreementApprovalChangeNotification) changeNotification;

    String targetHeiId = interInstitutionalAgreementApprovalChangeNotification.getPartnerHeiId();
    List<Integer> supportedMajorVersions = EwpApiUtils.getSupportedMajorVersions(
        getRegistryClient(), targetHeiId, EwpApi.INTERINSTITUTIONAL_AGREEMENTS_APPROVAL_CNR);

    if (supportedMajorVersions.contains(1)) {
      sendChangeNotificationVersion1(interInstitutionalAgreementApprovalChangeNotification);
    } else {
      throw new NoEwpCnrAPIException(changeNotification);
    }
  }

  private void sendChangeNotificationVersion1(
      EwpInterInstitutionalAgreementApprovalChangeNotification changeNotification)
      throws EwpClientErrorException {

    interInstitutionalAgreementApprovalCnrV1Client.sendChangeNotification(
        changeNotification.getApprovingHeiId(),
        changeNotification.getPartnerHeiId(),
        changeNotification.getOwnerHeiId(),
        changeNotification.getIiaId());
  }
}
