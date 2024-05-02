package pt.ulisboa.ewp.node.api.ewp.controller.iias;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.iias.v7.IiasV7;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV7HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInterInstitutionalAgreementsManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiInterInstitutionalAgreementsManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(
        InterInstitutionalAgreementsV7HostProvider.class, this::getManifestEntryForV7);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV7(
      String heiId,
      String baseUrl,
      Collection<InterInstitutionalAgreementsV7HostProvider> hostProviders) {
    IiasV7 manifestEntry = new IiasV7();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setIndexUrl(
        baseUrl
            + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
            + "/"
            + heiId
            + "/index");
    manifestEntry.setGetUrl(
        baseUrl + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH + "/" + heiId + "/get");
    manifestEntry.setStatsUrl(
        baseUrl
            + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
            + "/"
            + heiId
            + "/stats");

    int maxIiaIdsPerRequest =
        hostProviders.stream()
            .mapToInt(InterInstitutionalAgreementsV7HostProvider::getMaxIiaIdsPerRequest)
            .min()
            .orElse(0);
    manifestEntry.setMaxIiaIds(BigInteger.valueOf(maxIiaIdsPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
