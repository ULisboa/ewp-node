package pt.ulisboa.ewp.node.api.ewp.controller.imobilities.tors;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.imobilities.tors.v1.ImobilityTorsV1;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.OutgoingMobilityLearningAgreementsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiIncomingMobilityToRsManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiIncomingMobilityToRsManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(
        OutgoingMobilityLearningAgreementsV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<OutgoingMobilityLearningAgreementsV1HostProvider> hostProviders) {
    ImobilityTorsV1 manifestEntry = new ImobilityTorsV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry
        .setIndexUrl(
            baseUrl + EwpApiIncomingMobilityToRsV1Controller.BASE_PATH + "/index");
    manifestEntry
        .setGetUrl(
            baseUrl + EwpApiIncomingMobilityToRsV1Controller.BASE_PATH + "/get");

    int maxOutgoingMobilityIdsPerRequest = hostProviders.stream().mapToInt(
            OutgoingMobilityLearningAgreementsV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
