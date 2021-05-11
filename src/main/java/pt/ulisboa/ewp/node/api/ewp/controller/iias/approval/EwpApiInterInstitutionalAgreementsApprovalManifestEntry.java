package pt.ulisboa.ewp.node.api.ewp.controller.iias.approval;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalV1;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.InterInstitutionalAgreementsApprovalHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInterInstitutionalAgreementsApprovalManifestEntry
    implements EwpApiManifestEntryWithHttpSecurityOptionsStrategy {

  private final HostPluginManager hostPluginManager;

  public EwpApiInterInstitutionalAgreementsApprovalManifestEntry(
      HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @Override
  public Optional<ManifestApiEntryBaseV1> getManifestEntry(String heiId, String baseUrl) {
    Optional<InterInstitutionalAgreementsApprovalHostProvider> providerOptional =
        hostPluginManager
            .getProvider(heiId, InterInstitutionalAgreementsApprovalHostProvider.class);
    if (providerOptional.isEmpty()) {
      return Optional.empty();
    }
    InterInstitutionalAgreementsApprovalHostProvider hostProvider = providerOptional
        .get();

    IiasApprovalV1 manifestEntry = new IiasApprovalV1();
    manifestEntry.setVersion(EwpApiConstants.INTER_INSTITUTIONAL_AGREEMENTS_APPROVAL_VERSION);
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + "iias/approval");
    manifestEntry.setMaxIiaIds(BigInteger.valueOf(hostProvider.getMaxIiaIdsPerRequest()));
    manifestEntry.setHttpSecurity(getHttpSecurityOptions());
    return Optional.of(manifestEntry);
  }
}
