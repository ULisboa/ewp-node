package pt.ulisboa.ewp.node;

import java.util.Collection;
import java.util.concurrent.TimeUnit;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.scheduling.support.PeriodicTrigger;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.service.bootstrap.BootstrapService;
import pt.ulisboa.ewp.node.service.bootstrap.KeystoreBootstrapService;
import pt.ulisboa.ewp.node.service.ewp.mapping.sync.EwpMappingSyncService;
import pt.ulisboa.ewp.node.service.ewp.notification.EwpNotificationSenderDaemon;

/**
 * Components that runs once the application has started.
 * For instance, it bootstraps the domain.
 */
@Component
public class EwpNodeApplicationStartup implements ApplicationListener<ApplicationReadyEvent> {

  private final ThreadPoolTaskScheduler taskScheduler;

  private final EwpNotificationSenderDaemon ewpNotificationSenderDaemon;

  private final BootstrapService bootstrapService;
  private final KeystoreBootstrapService keystoreBootstrapService;

  private final Collection<EwpMappingSyncService> mappingSyncServices;

  private final FeatureFlags featureFlags;

  public EwpNodeApplicationStartup(@Autowired(required = false) ThreadPoolTaskScheduler taskScheduler,
      EwpNotificationSenderDaemon ewpNotificationSenderDaemon, BootstrapService bootstrapService,
      KeystoreBootstrapService keystoreBootstrapService,
      Collection<EwpMappingSyncService> mappingSyncServices, FeatureFlags featureFlags) {
    this.taskScheduler = taskScheduler;
    this.ewpNotificationSenderDaemon = ewpNotificationSenderDaemon;
    this.bootstrapService = bootstrapService;
    this.keystoreBootstrapService = keystoreBootstrapService;
    this.mappingSyncServices = mappingSyncServices;
    this.featureFlags = featureFlags;
  }

  @Override
  public void onApplicationEvent(ApplicationReadyEvent event) {
    this.bootstrapService.bootstrap();
    this.keystoreBootstrapService.bootstrap();

    if (this.featureFlags.isSchedulersEnabled()) {
      this.initSchedules();
    }
  }

  private void initSchedules() {
    taskScheduler.schedule(ewpNotificationSenderDaemon,
        new PeriodicTrigger(EwpNotificationSenderDaemon.TASK_INTERVAL_IN_MILLISECONDS,
            TimeUnit.MILLISECONDS));

    for (EwpMappingSyncService mappingSyncService : mappingSyncServices) {
      taskScheduler.schedule(mappingSyncService,
          new PeriodicTrigger(mappingSyncService.getTaskIntervalInMilliseconds(),
              TimeUnit.MILLISECONDS));
    }
  }

}
