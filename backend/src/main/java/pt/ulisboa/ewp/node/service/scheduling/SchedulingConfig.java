package pt.ulisboa.ewp.node.service.scheduling;

import java.util.Collection;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import pt.ulisboa.ewp.node.service.ewp.mapping.sync.EwpMappingSyncService;
import pt.ulisboa.ewp.node.service.ewp.notification.EwpNotificationSenderDaemon;
import pt.ulisboa.ewp.node.utils.IPUtils;

@Configuration
@EnableScheduling()
@ConditionalOnProperty(
    prefix = "scheduling",
    name = "enabled",
    havingValue = "true"
)
public class SchedulingConfig implements SchedulingConfigurer {

  private static final Logger LOG = LoggerFactory.getLogger(SchedulingConfig.class);

  private final String enableOnHostname;
  private final EwpNotificationSenderDaemon ewpNotificationSenderDaemon;
  private final Collection<EwpMappingSyncService> mappingSyncServices;

  public SchedulingConfig(
      @Value("${scheduling.enableOnHostname:#{null}}") String enableOnHostname,
      EwpNotificationSenderDaemon ewpNotificationSenderDaemon,
      Collection<EwpMappingSyncService> mappingSyncServices) {
    this.enableOnHostname = enableOnHostname;
    this.ewpNotificationSenderDaemon = ewpNotificationSenderDaemon;
    this.mappingSyncServices = mappingSyncServices;
  }

  @Bean
  public Executor taskExecutor() {
    return Executors.newScheduledThreadPool(2);
  }

  @Override
  public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
    if (this.enableOnHostname != null && !this.enableOnHostname.equals(IPUtils.getHostname())) {
      LOG.info(
          "[SCHEDULING] Will not schedule tasks as the current instance has hostname '{}' and is to be scheduled only for hostname '{}'",
          IPUtils.getHostname(),
          this.enableOnHostname);
      return;
    }

    taskRegistrar.setScheduler(taskExecutor());

    taskRegistrar.addTriggerTask(
        this.ewpNotificationSenderDaemon,
        this.ewpNotificationSenderDaemon::getNextExecutionInstant);

    for (EwpMappingSyncService mappingSyncService : mappingSyncServices) {
      taskRegistrar.addTriggerTask(mappingSyncService, mappingSyncService::getNextExecutionInstant);
    }

    LOG.info("[SCHEDULING] Scheduled tasks successfully");
  }
}
