package pt.ulisboa.ewp.node.service.ewp.mapping.sync;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia.Partner;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7;
import java.time.Instant;
import java.util.*;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV7HostProvider;
import pt.ulisboa.ewp.node.config.sync.SyncProperties;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.service.ewp.mapping.EwpInterInstitutionalAgreementMappingService;

/**
 * Service that synchronizes with external systems in order to obtain a current list of all IIAs
 * stored on those external systems.
 */
@Service
public class EwpInterInstitutionalAgreementMappingSyncService implements EwpMappingSyncService {

  private final SyncProperties syncProperties;
  private final HostPluginManager hostPluginManager;
  private final EwpInterInstitutionalAgreementMappingService mappingService;

  public EwpInterInstitutionalAgreementMappingSyncService(
      SyncProperties syncProperties, HostPluginManager hostPluginManager,
      EwpInterInstitutionalAgreementMappingService mappingService) {
    this.syncProperties = syncProperties;
    this.hostPluginManager = hostPluginManager;
    this.mappingService = mappingService;
  }

  @Override
  public void run() {
    syncInterInstitutionalAgreementsV7();
  }

  private void syncInterInstitutionalAgreementsV7() {
    Map<String, Collection<InterInstitutionalAgreementsV7HostProvider>> providersPerHeiId = hostPluginManager.getAllProvidersOfTypePerHeiId(
        InterInstitutionalAgreementsV7HostProvider.class);
    for (Map.Entry<String, Collection<InterInstitutionalAgreementsV7HostProvider>> entry : providersPerHeiId.entrySet()) {
      String heiId = entry.getKey();
      for (InterInstitutionalAgreementsV7HostProvider provider : entry.getValue()) {
        syncInterInstitutionalAgreementsOfHeiIdV7(heiId, provider);
      }
    }
  }

  private void syncInterInstitutionalAgreementsOfHeiIdV7(String heiId,
      InterInstitutionalAgreementsV7HostProvider provider) {
    Collection<String> iiaIds = provider.findAllIiaIdsByHeiId(heiId,
        heiId, null, null);
    for (String iiaId : iiaIds) {
      syncInterInstitutionalAgreementV7(heiId, provider, iiaId);
    }
  }

  private void syncInterInstitutionalAgreementV7(String heiId,
      InterInstitutionalAgreementsV7HostProvider provider,
      String iiaId) {
    Optional<EwpInterInstitutionalAgreementMapping> mappingOptional = this.mappingService.getMapping(
        heiId, iiaId);
    if (mappingOptional.isEmpty()) {
      Collection<IiasGetResponseV7.Iia> iias = provider.findByHeiIdAndIiaIds(heiId, heiId,
          Collections.singletonList(iiaId));
      registerMappingV7(heiId, iias.iterator().next());
    }
  }

  private void registerMappingV6(String heiId, Iia iia) {
    Partner partner = iia.getPartner().stream()
        .filter(p -> heiId.equals(p.getHeiId()))
        .findFirst()
        .orElse(null);
    assert partner != null;
    this.mappingService.registerMapping(heiId, partner.getOunitId(), partner.getIiaId());
  }

  private void registerMappingV7(String heiId, IiasGetResponseV7.Iia iia) {
    IiasGetResponseV7.Iia.Partner partner = iia.getPartner().stream()
        .filter(p -> heiId.equals(p.getHeiId()))
        .findFirst()
        .orElse(null);
    assert partner != null;
    this.mappingService.registerMapping(heiId, partner.getOunitId(), partner.getIiaId());
  }

  public Date getNextExecutionTime(TriggerContext context) {
    Optional<Date> lastCompletionTime = Optional.ofNullable(context.lastCompletionTime());
    Instant nextExecutionTime =
        lastCompletionTime
            .orElseGet(Date::new)
            .toInstant()
            .plusMillis(syncProperties.getMappings().getIntervalInMilliseconds());
    return Date.from(nextExecutionTime);
  }
}
