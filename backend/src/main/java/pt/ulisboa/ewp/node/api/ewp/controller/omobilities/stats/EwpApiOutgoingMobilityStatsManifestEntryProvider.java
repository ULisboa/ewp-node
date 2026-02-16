package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.stats;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.omobilities.stats.v1.OmobilityStatsV1;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.stats.OutgoingMobilityStatsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOutgoingMobilityStatsManifestEntryProvider extends EwpManifestEntryProvider {

  public EwpApiOutgoingMobilityStatsManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(
        OutgoingMobilityStatsV1HostProvider.class, this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(
      String sendingHeiId,
      String baseUrl,
      Collection<OutgoingMobilityStatsV1HostProvider> hostProviders) {
    OmobilityStatsV1 manifestEntry = new OmobilityStatsV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(
        baseUrl + EwpApiOutgoingMobilityStatsV1Controller.BASE_PATH + "/" + sendingHeiId);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
