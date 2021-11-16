package pt.ulisboa.ewp.node.service.ewp.mapping.sync;

import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.StudentMobilityForStudiesV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.StudentMobilityForStudiesV1.SendingHei;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.MockOutgoingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.service.ewp.mapping.EwpOutgoingMobilityMappingService;

class EwpOutgoingMobilityMappingSyncServiceUnitTest {

  @Test
  void testRun_TwoHostPluginsWithOneNewOutgoingMobilityIdEach_TwoMappingsArePersisted() {
    HostPluginManager hostPluginManager = Mockito.mock(HostPluginManager.class);
    EwpOutgoingMobilityMappingService mappingService = Mockito.mock(
        EwpOutgoingMobilityMappingService.class);
    EwpOutgoingMobilityMappingSyncService syncService = new EwpOutgoingMobilityMappingSyncService(
        hostPluginManager, mappingService);

    List<String> heiIds = Arrays.asList("h1", "h2");
    List<String> ounitIds = Arrays.asList("o1", "o2");
    List<String> omobilityIds = Arrays.asList("om1", "om1");

    List<StudentMobilityForStudiesV1> mobilities = new ArrayList<>();
    for (int index = 0; index < heiIds.size(); index++) {
      StudentMobilityForStudiesV1 mobility = new StudentMobilityForStudiesV1();
      mobility.setOmobilityId(omobilityIds.get(index));
      SendingHei sendingHei = new SendingHei();
      sendingHei.setHeiId(heiIds.get(index));
      sendingHei.setOunitId(ounitIds.get(index));
      mobility.setSendingHei(sendingHei);
      mobilities.add(mobility);
    }

    Map<String, Collection<MockOutgoingMobilitiesV1HostProvider>> providersPerHeiId = new HashMap<>();
    MockOutgoingMobilitiesV1HostProvider provider1 = Mockito.spy(
        new MockOutgoingMobilitiesV1HostProvider(
            1).registerOutgoingMobility(heiIds.get(0), omobilityIds.get(0), mobilities.get(0)));
    providersPerHeiId.put(heiIds.get(0), List.of(provider1));

    MockOutgoingMobilitiesV1HostProvider provider2 = Mockito.spy(
        new MockOutgoingMobilitiesV1HostProvider(
            1).registerOutgoingMobility(heiIds.get(1), omobilityIds.get(1), mobilities.get(1)));
    providersPerHeiId.put(heiIds.get(1), List.of(provider2));
    doReturn(providersPerHeiId).when(hostPluginManager)
        .getAllProvidersOfTypePerHeiId(OutgoingMobilitiesV1HostProvider.class);

    syncService.run();

    verify(provider1, times(1)).findBySendingHeiIdAndOutgoingMobilityIds(
        Collections.singletonList(heiIds.get(0)), heiIds.get(0),
        Collections.singletonList(omobilityIds.get(0)));
    verify(mappingService, times(1)).registerMapping(heiIds.get(0), ounitIds.get(0),
        omobilityIds.get(0));

    verify(provider2, times(1)).findBySendingHeiIdAndOutgoingMobilityIds(
        Collections.singletonList(heiIds.get(1)), heiIds.get(1),
        Collections.singletonList(omobilityIds.get(1)));
    verify(mappingService, times(1)).registerMapping(heiIds.get(1), ounitIds.get(1),
        omobilityIds.get(1));
  }

  @Test
  void testRun_TwoHostPluginsWithOneNewAndOneKnownOutgoingMobilityIds_OneNewMappingIsPersisted() {
    HostPluginManager hostPluginManager = Mockito.mock(HostPluginManager.class);
    EwpOutgoingMobilityMappingService mappingService = Mockito.mock(
        EwpOutgoingMobilityMappingService.class);
    EwpOutgoingMobilityMappingSyncService syncService = new EwpOutgoingMobilityMappingSyncService(
        hostPluginManager, mappingService);

    List<String> heiIds = Arrays.asList("h1", "h2");
    List<String> ounitIds = Arrays.asList("o1", "o2");
    List<String> omobilityIds = Arrays.asList("om1", "om1");

    List<StudentMobilityForStudiesV1> mobilities = new ArrayList<>();
    for (int index = 0; index < heiIds.size(); index++) {
      StudentMobilityForStudiesV1 mobility = new StudentMobilityForStudiesV1();
      mobility.setOmobilityId(omobilityIds.get(index));
      SendingHei sendingHei = new SendingHei();
      sendingHei.setHeiId(heiIds.get(index));
      sendingHei.setOunitId(ounitIds.get(index));
      mobility.setSendingHei(sendingHei);
      mobilities.add(mobility);
    }

    Map<String, Collection<MockOutgoingMobilitiesV1HostProvider>> providersPerHeiId = new HashMap<>();
    MockOutgoingMobilitiesV1HostProvider provider1 = Mockito.spy(
        new MockOutgoingMobilitiesV1HostProvider(
            1).registerOutgoingMobility(heiIds.get(0), omobilityIds.get(0), mobilities.get(0)));
    providersPerHeiId.put(heiIds.get(0), List.of(provider1));

    MockOutgoingMobilitiesV1HostProvider provider2 = Mockito.spy(
        new MockOutgoingMobilitiesV1HostProvider(
            1).registerOutgoingMobility(heiIds.get(1), omobilityIds.get(1), mobilities.get(1)));
    providersPerHeiId.put(heiIds.get(1), List.of(provider2));
    doReturn(providersPerHeiId).when(hostPluginManager)
        .getAllProvidersOfTypePerHeiId(OutgoingMobilitiesV1HostProvider.class);

    doReturn(Optional.of(
        EwpOutgoingMobilityMapping.create(heiIds.get(1), ounitIds.get(1), omobilityIds.get(1))))
        .when(mappingService).getMapping(heiIds.get(1), omobilityIds.get(1));

    syncService.run();

    verify(provider1, times(1)).findBySendingHeiIdAndOutgoingMobilityIds(
        Collections.singletonList(heiIds.get(0)), heiIds.get(0),
        Collections.singletonList(omobilityIds.get(0)));
    verify(mappingService, times(1)).registerMapping(heiIds.get(0), ounitIds.get(0),
        omobilityIds.get(0));

    verify(provider2, times(0)).findBySendingHeiIdAndOutgoingMobilityIds(
        Collections.singletonList(heiIds.get(0)), heiIds.get(1),
        Collections.singletonList(omobilityIds.get(1)));
    verify(mappingService, times(0)).registerMapping(heiIds.get(1), ounitIds.get(1),
        omobilityIds.get(1));
  }

}