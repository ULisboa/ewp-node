package pt.ulisboa.ewp.node.api.ewp.controller.imobilities;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.imobilities.v1.ImobilitiesV1;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.IncomingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiIncomingMobilitiesManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiIncomingMobilitiesManifestEntryProvider(
      HostPluginManager hostPluginManager, ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(
        IncomingMobilitiesV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<IncomingMobilitiesV1HostProvider> hostProviders) {
    ImobilitiesV1 manifestEntry = new ImobilitiesV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry
        .setGetUrl(baseUrl + EwpApiIncomingMobilitiesV1Controller.BASE_PATH + "/get");

    int maxOutgoingMobilityIdsPerRequest = hostProviders.stream().mapToInt(
            IncomingMobilitiesV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOmobilityIds(
        BigInteger.valueOf(maxOutgoingMobilityIdsPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    manifestEntry.setSendsNotifications(new EmptyV1());
    return manifestEntry;
  }
}
