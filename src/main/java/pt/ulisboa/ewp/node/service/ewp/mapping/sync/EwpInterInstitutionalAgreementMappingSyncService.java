package pt.ulisboa.ewp.node.service.ewp.mapping.sync;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia.Partner;
import java.time.Instant;
import java.util.*;
import org.springframework.scheduling.TriggerContext;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV6HostProvider;
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
    Map<String, Collection<InterInstitutionalAgreementsV6HostProvider>> providersPerHeiId = hostPluginManager.getAllProvidersOfTypePerHeiId(
        InterInstitutionalAgreementsV6HostProvider.class);
    for (Map.Entry<String, Collection<InterInstitutionalAgreementsV6HostProvider>> entry : providersPerHeiId.entrySet()) {
      String heiId = entry.getKey();
      for (InterInstitutionalAgreementsV6HostProvider provider : entry.getValue()) {
        syncInterInstitutionalAgreementsOfHeiId(heiId, provider);
      }
    }
  }

  private void syncInterInstitutionalAgreementsOfHeiId(String heiId,
      InterInstitutionalAgreementsV6HostProvider provider) {
    Collection<String> iiaIds = provider.findAllIiaIdsByHeiId(Collections.singletonList(heiId),
        heiId, null, null,
        null);
    for (String iiaId : iiaIds) {
      syncInterInstitutionalAgreement(heiId, provider, iiaId);
    }
  }

  private void syncInterInstitutionalAgreement(String heiId,
      InterInstitutionalAgreementsV6HostProvider provider,
      String iiaId) {
    Optional<EwpInterInstitutionalAgreementMapping> mappingOptional = this.mappingService.getMapping(
        heiId, iiaId);
    if (mappingOptional.isEmpty()) {
      Collection<Iia> iias = provider.findByHeiIdAndIiaIds(Collections.singletonList(heiId), heiId,
          Collections.singletonList(iiaId), false);
      registerMapping(heiId, iias.iterator().next());
    }
  }

  private void registerMapping(String heiId, Iia iia) {
    Partner partner = getPartnerByHeiId(heiId, iia);
    assert partner != null;
    this.mappingService.registerMapping(
        heiId, partner.getOunitId(), partner.getIiaId(), partner.getIiaCode());
  }

  private Partner getPartnerByHeiId(String heiId, Iia iia) {
    return iia.getPartner().stream()
        .filter(p -> heiId.equals(p.getHeiId()))
        .findFirst()
        .orElse(null);
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
