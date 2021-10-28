package pt.ulisboa.ewp.node.service.ewp.notification.handler;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.imobilities.cnr.EwpIncomingMobilityCnrV1Client;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpIncomingMobilityChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;

@Service
public class EwpIncomingMobilityChangeNotificationHandler extends
    EwpChangeNotificationHandler {

  private final EwpIncomingMobilityCnrV1Client incomingMobilityCnrV1Client;

  public EwpIncomingMobilityChangeNotificationHandler(
      RegistryClient registryClient,
      EwpIncomingMobilityCnrV1Client incomingMobilityCnrV1Client) {
    super(registryClient);
    this.incomingMobilityCnrV1Client = incomingMobilityCnrV1Client;
  }

  @Override
  public Class<?> getSupportedChangeNotificationClassType() {
    return EwpIncomingMobilityChangeNotification.class;
  }

  @Override
  public void sendChangeNotification(
      EwpChangeNotification changeNotification)
      throws EwpClientErrorException, NoEwpCnrAPIException {

    if (!(changeNotification instanceof EwpIncomingMobilityChangeNotification)) {
      throw new IllegalArgumentException("Invalid change notification type: " + changeNotification);
    }

    EwpIncomingMobilityChangeNotification incomingMobilityChangeNotification = (EwpIncomingMobilityChangeNotification) changeNotification;

    String targetHeiId = incomingMobilityChangeNotification.getSendingHeiId();
    List<Integer> supportedMajorVersions = EwpApiUtils.getSupportedMajorVersions(
        getRegistryClient(), targetHeiId,
        EwpApiConstants.API_INCOMING_MOBILITY_CNR_NAME);

    if (supportedMajorVersions.contains(1)) {
      sendChangeNotificationVersion1(incomingMobilityChangeNotification);
    } else {
      throw new NoEwpCnrAPIException(changeNotification);
    }
  }

  private void sendChangeNotificationVersion1(
      EwpIncomingMobilityChangeNotification changeNotification)
      throws EwpClientErrorException {

    incomingMobilityCnrV1Client.sendChangeNotification(
        changeNotification.getSendingHeiId(),
        changeNotification.getReceivingHeiId(),
        Collections.singletonList(changeNotification.getOutgoingMobilityId()));
  }
}
