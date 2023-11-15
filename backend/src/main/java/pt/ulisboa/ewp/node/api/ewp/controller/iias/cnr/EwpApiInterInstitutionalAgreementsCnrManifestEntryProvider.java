package pt.ulisboa.ewp.node.api.ewp.controller.iias.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.iias.cnr.v2.IiaCnrV2;
import eu.erasmuswithoutpaper.api.iias.cnr.v3.IiaCnrV3;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.cnr.InterInstitutionalAgreementCnrV2HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.cnr.InterInstitutionalAgreementCnrV3HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInterInstitutionalAgreementsCnrManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiInterInstitutionalAgreementsCnrManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(
        InterInstitutionalAgreementCnrV2HostProvider.class,
        this::getManifestEntryForV2);

    super.registerHostProviderToManifestEntryConverter(
        InterInstitutionalAgreementCnrV3HostProvider.class,
        this::getManifestEntryForV3);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV2(String heiId, String baseUrl,
      Collection<InterInstitutionalAgreementCnrV2HostProvider> hostProviders) {
    IiaCnrV2 manifestEntry = new IiaCnrV2();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiInterInstitutionalAgreementsCnrV2Controller.BASE_PATH);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV3(
      String heiId,
      String baseUrl,
      Collection<InterInstitutionalAgreementCnrV3HostProvider> hostProviders) {
    IiaCnrV3 manifestEntry = new IiaCnrV3();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiInterInstitutionalAgreementsCnrV3Controller.BASE_PATH + "?"
        + EwpApiParamConstants.HEI_ID
        + "="
        + heiId);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
