package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.omobilities.v2.OmobilitiesV2;
import eu.erasmuswithoutpaper.api.omobilities.v3.OmobilitiesV3;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV2HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV3HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOutgoingMobilitiesManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiOutgoingMobilitiesManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(
        OutgoingMobilitiesV2HostProvider.class, this::getManifestEntryForV2);

    super.registerHostProviderToManifestEntryConverter(
        OutgoingMobilitiesV3HostProvider.class, this::getManifestEntryForV3);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV2(String heiId, String baseUrl,
      Collection<OutgoingMobilitiesV2HostProvider> hostProviders) {
    OmobilitiesV2 manifestEntry = new OmobilitiesV2();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry
        .setIndexUrl(baseUrl + EwpApiOutgoingMobilitiesV2Controller.BASE_PATH + "/index");
    manifestEntry
        .setGetUrl(baseUrl + EwpApiOutgoingMobilitiesV2Controller.BASE_PATH + "/get");

    int maxOutgoingMobilityIdsPerRequest = hostProviders.stream().mapToInt(
            OutgoingMobilitiesV2HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    manifestEntry.setSendsNotifications(new EmptyV1());
    return manifestEntry;
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV3(
      String heiId, String baseUrl, Collection<OutgoingMobilitiesV3HostProvider> hostProviders) {
    OmobilitiesV3 manifestEntry = new OmobilitiesV3();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setIndexUrl(baseUrl + EwpApiOutgoingMobilitiesV3Controller.BASE_PATH + "/index");
    manifestEntry.setGetUrl(baseUrl + EwpApiOutgoingMobilitiesV3Controller.BASE_PATH + "/get");
    manifestEntry.setUpdateUrl(
        baseUrl + EwpApiOutgoingMobilitiesV3Controller.BASE_PATH + "/update");

    int maxOutgoingMobilityIdsPerRequest =
        hostProviders.stream()
            .mapToInt(OutgoingMobilitiesV3HostProvider::getMaxOutgoingMobilityIdsPerRequest)
            .min()
            .orElse(0);
    manifestEntry.setMaxOmobilityIds(BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
