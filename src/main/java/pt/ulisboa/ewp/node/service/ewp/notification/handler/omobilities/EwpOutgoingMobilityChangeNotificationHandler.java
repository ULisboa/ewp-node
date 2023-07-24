package pt.ulisboa.ewp.node.service.ewp.notification.handler.omobilities;

import java.util.Collections;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.omobilities.EwpOutgoingMobilityCnrV1Client;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpOutgoingMobilityChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;
import pt.ulisboa.ewp.node.service.ewp.notification.handler.EwpChangeNotificationHandler;
import pt.ulisboa.ewp.node.utils.EwpApi;

@Service
public class EwpOutgoingMobilityChangeNotificationHandler extends
        EwpChangeNotificationHandler {

  private final EwpOutgoingMobilityCnrV1Client outgoingMobilityCnrV1Client;

  public EwpOutgoingMobilityChangeNotificationHandler(
      RegistryClient registryClient,
      EwpOutgoingMobilityCnrV1Client outgoingMobilityCnrV1Client) {
    super(registryClient);
    this.outgoingMobilityCnrV1Client = outgoingMobilityCnrV1Client;
  }

  @Override
  public Class<?> getSupportedChangeNotificationClassType() {
    return EwpOutgoingMobilityChangeNotification.class;
  }

  @Override
  public void sendChangeNotification(
      EwpChangeNotification changeNotification)
      throws EwpClientErrorException, NoEwpCnrAPIException {

    if (!(changeNotification instanceof EwpOutgoingMobilityChangeNotification)) {
      throw new IllegalArgumentException("Invalid change notification type: " + changeNotification);
    }

    EwpOutgoingMobilityChangeNotification outgoingMobilityChangeNotification = (EwpOutgoingMobilityChangeNotification) changeNotification;

    String targetHeiId = outgoingMobilityChangeNotification.getReceivingHeiId();
    List<Integer> supportedMajorVersions = EwpApiUtils.getSupportedMajorVersions(
        getRegistryClient(), targetHeiId, EwpApi.OUTGOING_MOBILITY_CNR);

    if (supportedMajorVersions.contains(1)) {
      sendChangeNotificationVersion1(outgoingMobilityChangeNotification);
    } else {
      throw new NoEwpCnrAPIException(changeNotification);
    }
  }

  private void sendChangeNotificationVersion1(
      EwpOutgoingMobilityChangeNotification changeNotification)
      throws EwpClientErrorException {

    outgoingMobilityCnrV1Client.sendChangeNotification(
        changeNotification.getSendingHeiId(),
        changeNotification.getReceivingHeiId(),
        Collections.singletonList(changeNotification.getOutgoingMobilityId()));
  }
}
