package pt.ulisboa.ewp.node.service.ewp.notification.handler;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.omobilities.las.cnr.EwpOutgoingMobilityLearningAgreementCnrV1Client;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityLearningAgreementChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;

@Service
public class EwpOutgoingMobilityLearningAgreementChangeNotificationHandler extends
    EwpChangeNotificationHandler {

  private final EwpOutgoingMobilityLearningAgreementCnrV1Client outgoingMobilityLearningAgreementCnrV1Client;

  public EwpOutgoingMobilityLearningAgreementChangeNotificationHandler(
      RegistryClient registryClient,
      EwpOutgoingMobilityLearningAgreementCnrV1Client outgoingMobilityLearningAgreementCnrV1Client) {
    super(registryClient);
    this.outgoingMobilityLearningAgreementCnrV1Client = outgoingMobilityLearningAgreementCnrV1Client;
  }

  @Override
  public Class<?> getSupportedChangeNotificationClassType() {
    return EwpOutgoingMobilityLearningAgreementChangeNotification.class;
  }

  @Override
  public void sendChangeNotification(
      EwpChangeNotification changeNotification)
      throws EwpClientErrorException, NoEwpCnrAPIException {

    if (!(changeNotification instanceof EwpOutgoingMobilityLearningAgreementChangeNotification)) {
      throw new IllegalArgumentException("Invalid change notification type: " + changeNotification);
    }

    EwpOutgoingMobilityLearningAgreementChangeNotification outgoingMobilityLearningAgreementChangeNotification = (EwpOutgoingMobilityLearningAgreementChangeNotification) changeNotification;

    String targetHeiId = outgoingMobilityLearningAgreementChangeNotification.getReceivingHeiId();
    List<Integer> supportedMajorVersions = EwpApiUtils.getSupportedMajorVersions(
        getRegistryClient(), targetHeiId,
        EwpApiConstants.API_OUTGOING_MOBILITY_LEARNING_AGREEMENT_CNR_NAME);

    if (supportedMajorVersions.contains(1)) {
      sendChangeNotificationVersion1(outgoingMobilityLearningAgreementChangeNotification);
    } else {
      throw new NoEwpCnrAPIException(changeNotification);
    }
  }

  private void sendChangeNotificationVersion1(
      EwpOutgoingMobilityLearningAgreementChangeNotification changeNotification)
      throws EwpClientErrorException {

    outgoingMobilityLearningAgreementCnrV1Client.sendChangeNotification(
        changeNotification.getSendingHeiId(),
        changeNotification.getReceivingHeiId(),
        Collections.singletonList(
            changeNotification.getOutgoingMobilityId()));
  }
}
