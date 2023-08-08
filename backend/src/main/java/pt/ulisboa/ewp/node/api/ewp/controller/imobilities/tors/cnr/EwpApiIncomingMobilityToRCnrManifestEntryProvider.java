package pt.ulisboa.ewp.node.api.ewp.controller.imobilities.tors.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.imobilities.tors.cnr.v1.ImobilityTorCnrV1;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.tors.cnr.IncomingMobilityToRCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiIncomingMobilityToRCnrManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiIncomingMobilityToRCnrManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(
        IncomingMobilityToRCnrV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<IncomingMobilityToRCnrV1HostProvider> hostProviders) {
    ImobilityTorCnrV1 manifestEntry = new ImobilityTorCnrV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(
        baseUrl + EwpApiIncomingMobilityToRCnrV1Controller.BASE_PATH);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());

    int maxOutgoingMobilityIdsPerRequest = hostProviders.stream().mapToInt(
            IncomingMobilityToRCnrV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    return manifestEntry;
  }
}
