package pt.ulisboa.ewp.node;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.service.bootstrap.BootstrapService;
import pt.ulisboa.ewp.node.service.bootstrap.KeystoreBootstrapService;

/**
 * Components that runs once the application has started.
 * For instance, it bootstraps the domain.
 */
@Component
public class EwpNodeApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

  private final BootstrapService bootstrapService;
  private final KeystoreBootstrapService keystoreBootstrapService;

  public EwpNodeApplicationStartup(BootstrapService bootstrapService,
      KeystoreBootstrapService keystoreBootstrapService) {
    this.bootstrapService = bootstrapService;
    this.keystoreBootstrapService = keystoreBootstrapService;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    this.bootstrapService.bootstrap();
    this.keystoreBootstrapService.bootstrap();
  }

}
