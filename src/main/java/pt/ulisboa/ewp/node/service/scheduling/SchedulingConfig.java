package pt.ulisboa.ewp.node.service.scheduling;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import pt.ulisboa.ewp.node.service.ewp.mapping.sync.EwpMappingSyncService;
import pt.ulisboa.ewp.node.service.ewp.notification.EwpNotificationSenderDaemon;

@Configuration
@EnableScheduling()
@ConditionalOnProperty(
    prefix = "scheduling",
    name = "enabled",
    havingValue = "true"
)
public class SchedulingConfig implements SchedulingConfigurer {

  private final EwpNotificationSenderDaemon ewpNotificationSenderDaemon;
  private final Collection<EwpMappingSyncService> mappingSyncServices;

  public SchedulingConfig(
      EwpNotificationSenderDaemon ewpNotificationSenderDaemon,
      Collection<EwpMappingSyncService> mappingSyncServices) {
    this.ewpNotificationSenderDaemon = ewpNotificationSenderDaemon;
    this.mappingSyncServices = mappingSyncServices;
  }

  @Bean
  public Executor taskExecutor() {
    return Executors.newScheduledThreadPool(2);
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    taskRegistrar.setScheduler(taskExecutor());

    taskRegistrar.addTriggerTask(
        this.ewpNotificationSenderDaemon, this.ewpNotificationSenderDaemon::getNextExecutionTime);

    for (EwpMappingSyncService mappingSyncService : mappingSyncServices) {
      taskRegistrar.addTriggerTask(mappingSyncService, mappingSyncService::getNextExecutionTime);
    }
  }
}
