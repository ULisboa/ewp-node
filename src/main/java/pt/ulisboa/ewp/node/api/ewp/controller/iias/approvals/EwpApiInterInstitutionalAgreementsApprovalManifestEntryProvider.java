package pt.ulisboa.ewp.node.api.ewp.controller.iias.approvals;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalV1;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.InterInstitutionalAgreementsApprovalV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInterInstitutionalAgreementsApprovalManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiInterInstitutionalAgreementsApprovalManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(
        InterInstitutionalAgreementsApprovalV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<InterInstitutionalAgreementsApprovalV1HostProvider> hostProviders) {
    IiasApprovalV1 manifestEntry = new IiasApprovalV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry
        .setUrl(baseUrl + EwpApiInterInstitutionalAgreementsApprovalV1Controller.BASE_PATH);

    int maxIiaIdsPerRequest = hostProviders.stream().mapToInt(
            InterInstitutionalAgreementsApprovalV1HostProvider::getMaxIiaIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxIiaIds(BigInteger.valueOf(maxIiaIdsPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
