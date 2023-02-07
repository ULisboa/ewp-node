package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.las.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrV1;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.cnr.OutgoingMobilityLearningAgreementCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOutgoingMobilityLearningAgreementCnrManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiOutgoingMobilityLearningAgreementCnrManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(
        OutgoingMobilityLearningAgreementCnrV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<OutgoingMobilityLearningAgreementCnrV1HostProvider> hostProviders) {
    OmobilityLaCnrV1 manifestEntry = new OmobilityLaCnrV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(
        baseUrl + EwpApiOutgoingMobilityLearningAgreementCnrV1Controller.BASE_PATH);
    manifestEntry.setStatsUrl(
        baseUrl + EwpApiOutgoingMobilityLearningAgreementCnrV1Controller.BASE_PATH + "/stats?"
            + EwpApiParamConstants.HEI_ID + "="
            + heiId);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());

    int maxOutgoingMobilityIdsPerRequest = hostProviders.stream().mapToInt(
            OutgoingMobilityLearningAgreementCnrV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    return manifestEntry;
  }
}
