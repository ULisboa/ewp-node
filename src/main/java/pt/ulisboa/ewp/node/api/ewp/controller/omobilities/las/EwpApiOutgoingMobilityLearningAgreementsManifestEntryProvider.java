package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.las;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.OmobilityLasV1;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.OutgoingMobilityLearningAgreementsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOutgoingMobilityLearningAgreementsManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiOutgoingMobilityLearningAgreementsManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(
        OutgoingMobilityLearningAgreementsV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<OutgoingMobilityLearningAgreementsV1HostProvider> hostProviders) {
    OmobilityLasV1 manifestEntry = new OmobilityLasV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry
        .setIndexUrl(
            baseUrl + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH + "/index");
    manifestEntry
        .setGetUrl(
            baseUrl + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH + "/get");
    manifestEntry
        .setUpdateUrl(
            baseUrl + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH + "/update");
    manifestEntry.setStatsUrl(
        baseUrl + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH + "/stats?"
            + EwpApiParamConstants.HEI_ID + "="
            + heiId);

    int maxOutgoingMobilityIdsPerRequest = hostProviders.stream().mapToInt(
            OutgoingMobilityLearningAgreementsV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
