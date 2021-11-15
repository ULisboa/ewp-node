package pt.ulisboa.ewp.node.service.ewp.mapping.sync;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia.Partner;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV6HostProvider;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.service.ewp.mapping.EwpInterInstitutionalAgreementMappingService;

/**
 * Service that synchronizes with external systems in order to obtain a current list of all IIAs
 * stored on those external systems.
 */
@Service
public class EwpInterInstitutionalAgreementsMappingSyncService implements EwpMappingSyncService {

  // TODO allow to set this by setting
  public static final int TASK_INTERVAL_IN_MILLISECONDS = 30 * 60 * 1000; // 30 minutes

  private final HostPluginManager hostPluginManager;
  private final EwpInterInstitutionalAgreementMappingService mappingService;

  public EwpInterInstitutionalAgreementsMappingSyncService(
      HostPluginManager hostPluginManager,
      EwpInterInstitutionalAgreementMappingService mappingService) {
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
    Collection<String> iiaIds = provider.findAllIiaIdsByHeiId(heiId, null, null,
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
      Collection<Iia> iias = provider.findByHeiIdAndIiaIds(heiId,
          Collections.singletonList(iiaId), false);
      registerMapping(heiId, iias.iterator().next());
    }
  }

  private void registerMapping(String heiId, Iia iia) {
    Partner partner = getPartnerByHeiId(heiId, iia);
    assert partner != null;
    this.mappingService.registerMapping(heiId, partner.getOunitId(), partner.getIiaId(),
        partner.getIiaCode());
  }

  private Partner getPartnerByHeiId(String heiId, Iia iia) {
    return iia.getPartner().stream().filter(p -> heiId.equals(p.getHeiId())).findFirst()
        .orElse(null);
  }

  @Override
  public long getTaskIntervalInMilliseconds() {
    return TASK_INTERVAL_IN_MILLISECONDS;
  }
}
