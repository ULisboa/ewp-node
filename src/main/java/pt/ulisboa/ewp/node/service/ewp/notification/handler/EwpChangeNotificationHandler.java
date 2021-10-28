package pt.ulisboa.ewp.node.service.ewp.notification.handler;

import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.notification.EwpChangeNotification;
import pt.ulisboa.ewp.node.service.ewp.notification.exception.NoEwpCnrAPIException;

@Service
public abstract class EwpChangeNotificationHandler {

  private final RegistryClient registryClient;

  protected EwpChangeNotificationHandler(RegistryClient registryClient) {
    this.registryClient = registryClient;
  }

  public abstract Class<?> getSupportedChangeNotificationClassType();

  public abstract void sendChangeNotification(EwpChangeNotification changeNotification)
      throws EwpClientErrorException, NoEwpCnrAPIException;

  protected RegistryClient getRegistryClient() {
    return registryClient;
  }

}
