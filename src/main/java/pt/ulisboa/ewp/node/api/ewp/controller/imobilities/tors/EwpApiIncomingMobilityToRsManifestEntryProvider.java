package pt.ulisboa.ewp.node.api.ewp.controller.imobilities.tors;

import eu.erasmuswithoutpaper.api.imobilities.tors.v1.ImobilityTorsV1;
import eu.erasmuswithoutpaper.api.imobilities.tors.v2.ImobilityTorsV2;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.tors.IncomingMobilityToRsV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.tors.IncomingMobilityToRsV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiIncomingMobilityToRsManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiIncomingMobilityToRsManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(
        IncomingMobilityToRsV1HostProvider.class,
        this::getManifestEntryForV1);

    super.registerHostProviderToManifestEntryConverter(
        IncomingMobilityToRsV2HostProvider.class,
        this::getManifestEntryForV2);
  }

  public ImobilityTorsV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<IncomingMobilityToRsV1HostProvider> hostProviders) {
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
            IncomingMobilityToRsV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }

  public ImobilityTorsV2 getManifestEntryForV2(String heiId, String baseUrl,
      Collection<IncomingMobilityToRsV2HostProvider> hostProviders) {
    ImobilityTorsV2 manifestEntry = new ImobilityTorsV2();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry
        .setIndexUrl(
            baseUrl + EwpApiIncomingMobilityToRsV2Controller.BASE_PATH + "/index");
    manifestEntry
        .setGetUrl(
            baseUrl + EwpApiIncomingMobilityToRsV2Controller.BASE_PATH + "/get");

    int maxOutgoingMobilityIdsPerRequest = hostProviders.stream().mapToInt(
            IncomingMobilityToRsV2HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
