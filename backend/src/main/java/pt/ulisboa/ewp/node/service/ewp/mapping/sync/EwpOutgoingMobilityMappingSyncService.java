package pt.ulisboa.ewp.node.service.ewp.mapping.sync;

import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.StudentMobilityForStudiesV1;
import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.StudentMobilityV2;
import java.time.Instant;
import java.util.*;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV2HostProvider;
import pt.ulisboa.ewp.node.config.sync.SyncProperties;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.service.ewp.mapping.EwpOutgoingMobilityMappingService;

/**
 * Service that synchronizes with external systems in order to obtain a current list of all Outgoing
 * Mobilities stored on those external systems.
 */
@Service
public class EwpOutgoingMobilityMappingSyncService implements EwpMappingSyncService {

  private final SyncProperties syncProperties;
  private final HostPluginManager hostPluginManager;
  private final EwpOutgoingMobilityMappingService mappingService;

  public EwpOutgoingMobilityMappingSyncService(
      SyncProperties syncProperties, HostPluginManager hostPluginManager,
      EwpOutgoingMobilityMappingService mappingService) {
    this.syncProperties = syncProperties;
    this.hostPluginManager = hostPluginManager;
    this.mappingService = mappingService;
  }

  @Override
  public void run() {
    syncOutgoingMobilitiesFromProvidersV1();
    syncOutgoingMobilitiesFromProvidersV2();
  }

  private void syncOutgoingMobilitiesFromProvidersV1() {
    Map<String, Collection<OutgoingMobilitiesV1HostProvider>> providersPerHeiId = hostPluginManager.getAllProvidersOfTypePerHeiId(
        OutgoingMobilitiesV1HostProvider.class);
    for (Map.Entry<String, Collection<OutgoingMobilitiesV1HostProvider>> entry : providersPerHeiId.entrySet()) {
      String heiId = entry.getKey();
      for (OutgoingMobilitiesV1HostProvider provider : entry.getValue()) {
        syncOutgoingMobilitiesOfHeiIdFromProviderV1(heiId, provider);
      }
    }
  }

  private void syncOutgoingMobilitiesFromProvidersV2() {
    Map<String, Collection<OutgoingMobilitiesV2HostProvider>> providersPerHeiId = hostPluginManager.getAllProvidersOfTypePerHeiId(
        OutgoingMobilitiesV2HostProvider.class);
    for (Map.Entry<String, Collection<OutgoingMobilitiesV2HostProvider>> entry : providersPerHeiId.entrySet()) {
      String heiId = entry.getKey();
      for (OutgoingMobilitiesV2HostProvider provider : entry.getValue()) {
        syncOutgoingMobilitiesOfHeiIdFromProviderV2(heiId, provider);
      }
    }
  }

  private void syncOutgoingMobilitiesOfHeiIdFromProviderV1(String heiId,
      OutgoingMobilitiesV1HostProvider provider) {
    Collection<String> outgoingMobilityIds = provider.findOutgoingMobilityIds(
        Collections.singletonList(heiId), heiId, null, null,
        null);
    for (String outgoingMobilityId : outgoingMobilityIds) {
      syncOutgoingMobilityFromProviderV1(heiId, provider, outgoingMobilityId);
    }
  }

  private void syncOutgoingMobilitiesOfHeiIdFromProviderV2(String heiId,
      OutgoingMobilitiesV2HostProvider provider) {
    Collection<String> outgoingMobilityIds = provider.findOutgoingMobilityIds(
        Collections.singletonList(heiId), heiId, null, null,
        null);
    for (String outgoingMobilityId : outgoingMobilityIds) {
      syncOutgoingMobilityFromProviderV2(heiId, provider, outgoingMobilityId);
    }
  }

  private void syncOutgoingMobilityFromProviderV1(String heiId,
      OutgoingMobilitiesV1HostProvider provider,
      String outgoingMobilityId) {
    Optional<EwpOutgoingMobilityMapping> mappingOptional = this.mappingService.getMapping(
        heiId, outgoingMobilityId);
    if (mappingOptional.isEmpty()) {
      Collection<StudentMobilityForStudiesV1> outgoingMobilities = provider.findBySendingHeiIdAndOutgoingMobilityIds(
          Collections.singletonList(heiId), heiId, Collections.singletonList(outgoingMobilityId));
      registerMapping(heiId, outgoingMobilities.iterator().next());
    }
  }

  private void syncOutgoingMobilityFromProviderV2(String heiId,
      OutgoingMobilitiesV2HostProvider provider,
      String outgoingMobilityId) {
    Optional<EwpOutgoingMobilityMapping> mappingOptional = this.mappingService.getMapping(
        heiId, outgoingMobilityId);
    if (mappingOptional.isEmpty()) {
      Collection<StudentMobilityV2> outgoingMobilities = provider.findBySendingHeiIdAndOutgoingMobilityIds(
          Collections.singletonList(heiId), heiId, Collections.singletonList(outgoingMobilityId));
      registerMapping(heiId, outgoingMobilities.iterator().next());
    }
  }

  private void registerMapping(String heiId, StudentMobilityForStudiesV1 outgoingMobility) {
    this.mappingService.registerMapping(heiId, outgoingMobility.getSendingHei().getOunitId(),
        outgoingMobility.getOmobilityId());
  }

  private void registerMapping(String heiId, StudentMobilityV2 outgoingMobility) {
    this.mappingService.registerMapping(heiId, outgoingMobility.getSendingHei().getOunitId(),
        outgoingMobility.getOmobilityId());
  }

  public Instant getNextExecutionInstant(TriggerContext context) {
    Optional<Instant> lastCompletionTime = Optional.ofNullable(context.lastCompletion());
    return lastCompletionTime
        .orElseGet(Instant::now)
        .plusMillis(syncProperties.getMappings().getIntervalInMilliseconds());
  }
}
