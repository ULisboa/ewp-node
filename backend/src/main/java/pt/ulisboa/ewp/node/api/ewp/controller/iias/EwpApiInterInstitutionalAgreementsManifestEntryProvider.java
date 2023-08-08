package pt.ulisboa.ewp.node.api.ewp.controller.iias;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.iias.v6.IiasV6;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV6HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInterInstitutionalAgreementsManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiInterInstitutionalAgreementsManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(
        InterInstitutionalAgreementsV6HostProvider.class,
        this::getManifestEntryForV6);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV6(String heiId, String baseUrl,
      Collection<InterInstitutionalAgreementsV6HostProvider> hostProviders) {
    IiasV6 manifestEntry = new IiasV6();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry
        .setIndexUrl(baseUrl + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH + "/index");
    manifestEntry
        .setGetUrl(baseUrl + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH + "/get");
    manifestEntry.setStatsUrl(
        baseUrl + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH + "/stats?" +
            EwpApiParamConstants.HEI_ID + "=" + heiId);

    int maxIiaIdsPerRequest = hostProviders.stream().mapToInt(
            InterInstitutionalAgreementsV6HostProvider::getMaxIiaIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxIiaIds(BigInteger.valueOf(maxIiaIdsPerRequest));

    int maxIiaCodesPerRequest = hostProviders.stream().mapToInt(
            InterInstitutionalAgreementsV6HostProvider::getMaxIiaCodesPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxIiaCodes(BigInteger.valueOf(maxIiaCodesPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
