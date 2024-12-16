package pt.ulisboa.ewp.node.service.ewp;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.List;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import pt.ulisboa.ewp.node.service.ewp.mapping.sync.EwpMappingSyncService;
import pt.ulisboa.ewp.node.service.ewp.notification.EwpNotificationSenderDaemon;
import pt.ulisboa.ewp.node.service.scheduling.SchedulingConfig;
import pt.ulisboa.ewp.node.utils.IPUtils;

public class SchedulingConfigTest {

  @Test
  public void testConfigureTasks_HostnameRestrictionIsNull_TasksScheduled() {
    EwpNotificationSenderDaemon mockEwpNotificationSenderDaemon =
        Mockito.mock(EwpNotificationSenderDaemon.class);
    List<EwpMappingSyncService> mockEwpMappingSyncServices =
        List.of(Mockito.mock(EwpMappingSyncService.class));
    SchedulingConfig schedulingConfig =
        new SchedulingConfig(null, mockEwpNotificationSenderDaemon, mockEwpMappingSyncServices);

    ScheduledTaskRegistrar scheduledTaskRegistrar = Mockito.mock(ScheduledTaskRegistrar.class);

    schedulingConfig.configureTasks(scheduledTaskRegistrar);

    verify(scheduledTaskRegistrar, times(1)).setScheduler(Mockito.any());

    verify(scheduledTaskRegistrar, times(1))
        .addTriggerTask(Mockito.eq(mockEwpNotificationSenderDaemon), Mockito.any());

    for (EwpMappingSyncService mockEwpMappingSyncService : mockEwpMappingSyncServices) {
      verify(scheduledTaskRegistrar, times(1))
          .addTriggerTask(Mockito.eq(mockEwpMappingSyncService), Mockito.any());
    }
  }

  @Test
  public void testConfigureTasks_CurrentHostnameMatchesHostnameRestriction_TasksScheduled() {
    EwpNotificationSenderDaemon mockEwpNotificationSenderDaemon =
        Mockito.mock(EwpNotificationSenderDaemon.class);
    List<EwpMappingSyncService> mockEwpMappingSyncServices =
        List.of(Mockito.mock(EwpMappingSyncService.class));
    String hostname = "ewp-node-test-1";
    try (MockedStatic<IPUtils> mockedIPUtils = Mockito.mockStatic(IPUtils.class)) {
      mockedIPUtils.when(IPUtils::getHostname).thenReturn(hostname);

      SchedulingConfig schedulingConfig =
          new SchedulingConfig(
              hostname, mockEwpNotificationSenderDaemon, mockEwpMappingSyncServices);

      ScheduledTaskRegistrar scheduledTaskRegistrar = Mockito.mock(ScheduledTaskRegistrar.class);

      schedulingConfig.configureTasks(scheduledTaskRegistrar);

      verify(scheduledTaskRegistrar, times(1)).setScheduler(Mockito.any());

      verify(scheduledTaskRegistrar, times(1))
          .addTriggerTask(Mockito.eq(mockEwpNotificationSenderDaemon), Mockito.any());

      for (EwpMappingSyncService mockEwpMappingSyncService : mockEwpMappingSyncServices) {
        verify(scheduledTaskRegistrar, times(1))
            .addTriggerTask(Mockito.eq(mockEwpMappingSyncService), Mockito.any());
      }
    }
  }

  @Test
  public void testConfigureTasks_CurrentHostnameNotMatchesHostnameRestriction_TasksAreScheduled() {
    EwpNotificationSenderDaemon mockEwpNotificationSenderDaemon =
        Mockito.mock(EwpNotificationSenderDaemon.class);
    List<EwpMappingSyncService> mockEwpMappingSyncServices =
        List.of(Mockito.mock(EwpMappingSyncService.class));
    String hostname = "ewp-node-test-1";
    try (MockedStatic<IPUtils> mockedIPUtils = Mockito.mockStatic(IPUtils.class)) {
      mockedIPUtils.when(IPUtils::getHostname).thenReturn(hostname);

      SchedulingConfig schedulingConfig =
          new SchedulingConfig(
              "wrong-hostname", mockEwpNotificationSenderDaemon, mockEwpMappingSyncServices);

      ScheduledTaskRegistrar scheduledTaskRegistrar = Mockito.mock(ScheduledTaskRegistrar.class);

      schedulingConfig.configureTasks(scheduledTaskRegistrar);

      verify(scheduledTaskRegistrar, times(0)).setScheduler(Mockito.any());

      verify(scheduledTaskRegistrar, times(0))
          .addTriggerTask(Mockito.eq(mockEwpNotificationSenderDaemon), Mockito.any());

      for (EwpMappingSyncService mockEwpMappingSyncService : mockEwpMappingSyncServices) {
        verify(scheduledTaskRegistrar, times(0))
            .addTriggerTask(Mockito.eq(mockEwpMappingSyncService), Mockito.any());
      }
    }
  }
}
