package pt.ulisboa.ewp.node.service.ewp.mapping.sync;

import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.StudentMobilityForStudiesV1;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.service.ewp.mapping.EwpOutgoingMobilityMappingService;

/**
 * Service that synchronizes with external systems in order to obtain a current list of all Outgoing
 * Mobilities stored on those external systems.
 */
@Service
public class EwpOutgoingMobilityMappingSyncService implements EwpMappingSyncService {

  // TODO allow to set this by setting
  public static final int TASK_INTERVAL_IN_MILLISECONDS = 30 * 60 * 1000; // 30 minutes

  private final HostPluginManager hostPluginManager;
  private final EwpOutgoingMobilityMappingService mappingService;

  public EwpOutgoingMobilityMappingSyncService(
      HostPluginManager hostPluginManager,
      EwpOutgoingMobilityMappingService mappingService) {
    this.hostPluginManager = hostPluginManager;
    this.mappingService = mappingService;
  }

  @Override
  public void run() {
    Map<String, Collection<OutgoingMobilitiesV1HostProvider>> providersPerHeiId = hostPluginManager.getAllProvidersOfTypePerHeiId(
        OutgoingMobilitiesV1HostProvider.class);
    for (Map.Entry<String, Collection<OutgoingMobilitiesV1HostProvider>> entry : providersPerHeiId.entrySet()) {
      String heiId = entry.getKey();
      for (OutgoingMobilitiesV1HostProvider provider : entry.getValue()) {
        syncOutgoingMobilitiesOfHeiId(heiId, provider);
      }
    }
  }

  private void syncOutgoingMobilitiesOfHeiId(String heiId,
      OutgoingMobilitiesV1HostProvider provider) {
    Collection<String> outgoingMobilityIds = provider.findOutgoingMobilityIds(
        Collections.singletonList(heiId), heiId, null, null,
        null);
    for (String outgoingMobilityId : outgoingMobilityIds) {
      syncOutgoingMobility(heiId, provider, outgoingMobilityId);
    }
  }

  private void syncOutgoingMobility(String heiId,
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

  private void registerMapping(String heiId, StudentMobilityForStudiesV1 outgoingMobility) {
    this.mappingService.registerMapping(heiId, outgoingMobility.getSendingHei().getOunitId(),
        outgoingMobility.getOmobilityId());
  }

  @Override
  public long getTaskIntervalInMilliseconds() {
    return TASK_INTERVAL_IN_MILLISECONDS;
  }
}
