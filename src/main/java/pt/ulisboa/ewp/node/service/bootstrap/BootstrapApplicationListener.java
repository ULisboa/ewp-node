package pt.ulisboa.ewp.node.service.bootstrap;

import javax.transaction.Transactional;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

/**
 * Listens for application ready event, and then bootstraps the domain.
 */
@Component
public class BootstrapApplicationListener implements ApplicationListener<ApplicationReadyEvent> {

  private final HostsBootstrapService hostsBootstrapService;
  private final KeystoreBootstrapService keystoreBootstrapService;

  public BootstrapApplicationListener(HostsBootstrapService hostsBootstrapService, KeystoreBootstrapService keystoreBootstrapService) {
    this.hostsBootstrapService = hostsBootstrapService;
    this.keystoreBootstrapService = keystoreBootstrapService;
  }

  @Override
  @Transactional
  public void onApplicationEvent(ApplicationReadyEvent event) {
    this.hostsBootstrapService.bootstrap();
    this.keystoreBootstrapService.bootstrap();
  }
}
