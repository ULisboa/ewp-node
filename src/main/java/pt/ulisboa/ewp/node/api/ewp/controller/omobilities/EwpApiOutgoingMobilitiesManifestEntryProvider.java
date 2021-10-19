package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.OmobilitiesV1;
import java.math.BigInteger;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOutgoingMobilitiesManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiOutgoingMobilitiesManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(
        OutgoingMobilitiesV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      OutgoingMobilitiesV1HostProvider hostProvider) {
    OmobilitiesV1 manifestEntry = new OmobilitiesV1();
    manifestEntry.setVersion(hostProvider.getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry
        .setIndexUrl(baseUrl + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH + "/index");
    manifestEntry
        .setGetUrl(baseUrl + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH + "/get");
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(hostProvider.getMaxOutgoingMobilityIdsPerRequest()));
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
