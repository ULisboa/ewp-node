package pt.ulisboa.ewp.node.service.ewp.notification.handler.imobilities.tors;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.imobilities.tors.cnr.EwpIncomingMobilityToRCnrV1Client;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpIncomingMobilityToRChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.service.ewp.notification.handler.EwpChangeNotificationHandler;
import pt.ulisboa.ewp.node.utils.EwpApi;

@Service
public class EwpIncomingMobilityToRChangeNotificationHandler extends
        EwpChangeNotificationHandler {

  private final EwpIncomingMobilityToRCnrV1Client incomingMobilityToRCnrV1Client;

  public EwpIncomingMobilityToRChangeNotificationHandler(
      RegistryClient registryClient,
      EwpIncomingMobilityToRCnrV1Client incomingMobilityToRCnrV1Client) {
    super(registryClient);
    this.incomingMobilityToRCnrV1Client = incomingMobilityToRCnrV1Client;
  }

  @Override
  public Class<?> getSupportedChangeNotificationClassType() {
    return EwpIncomingMobilityToRChangeNotification.class;
  }

  @Override
  public void sendChangeNotification(
      EwpChangeNotification changeNotification)
      throws EwpClientErrorException, NoEwpCnrAPIException {

    if (!(changeNotification instanceof EwpIncomingMobilityToRChangeNotification)) {
      throw new IllegalArgumentException("Invalid change notification type: " + changeNotification);
    }

    EwpIncomingMobilityToRChangeNotification incomingMobilityToRChangeNotification = (EwpIncomingMobilityToRChangeNotification) changeNotification;

    String targetHeiId = incomingMobilityToRChangeNotification.getSendingHeiId();
    List<Integer> supportedMajorVersions = EwpApiUtils.getSupportedMajorVersions(
        getRegistryClient(), targetHeiId, EwpApi.INCOMING_MOBILITY_TOR_CNR);

    if (supportedMajorVersions.contains(1)) {
      sendChangeNotificationVersion1(incomingMobilityToRChangeNotification);
    } else {
      throw new NoEwpCnrAPIException(changeNotification);
    }
  }

  private void sendChangeNotificationVersion1(
      EwpIncomingMobilityToRChangeNotification changeNotification)
      throws EwpClientErrorException {

    incomingMobilityToRCnrV1Client.sendChangeNotification(
        changeNotification.getSendingHeiId(),
        changeNotification.getReceivingHeiId(),
        Collections.singletonList(changeNotification.getOutgoingMobilityId()));
  }
}
