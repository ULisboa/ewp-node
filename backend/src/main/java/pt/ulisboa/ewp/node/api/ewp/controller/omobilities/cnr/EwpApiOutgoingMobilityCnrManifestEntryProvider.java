package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.omobilities.cnr.v1.OmobilityCnrV1;
import eu.erasmuswithoutpaper.api.omobilities.cnr.v2.OmobilityCnrV2;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.cnr.OutgoingMobilityCnrV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.cnr.OutgoingMobilityCnrV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOutgoingMobilityCnrManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiOutgoingMobilityCnrManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(OutgoingMobilityCnrV1HostProvider.class,
        this::getManifestEntryForV1);

    super.registerHostProviderToManifestEntryConverter(
        OutgoingMobilityCnrV2HostProvider.class, this::getManifestEntryForV2);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<OutgoingMobilityCnrV1HostProvider> hostProviders) {
    OmobilityCnrV1 manifestEntry = new OmobilityCnrV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiOutgoingMobilityCnrV1Controller.BASE_PATH);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());

    int maxOutgoingMobilityIdsPerRequest = hostProviders.stream().mapToInt(
            OutgoingMobilityCnrV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    return manifestEntry;
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV2(
      String heiId, String baseUrl, Collection<OutgoingMobilityCnrV2HostProvider> hostProviders) {
    OmobilityCnrV2 manifestEntry = new OmobilityCnrV2();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiOutgoingMobilityCnrV2Controller.BASE_PATH + "/" + heiId);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());

    int maxOutgoingMobilityIdsPerRequest =
        hostProviders.stream()
            .mapToInt(OutgoingMobilityCnrV2HostProvider::getMaxOutgoingMobilityIdsPerRequest)
            .min()
            .orElse(0);
    manifestEntry.setMaxOmobilityIds(BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    return manifestEntry;
  }
}
