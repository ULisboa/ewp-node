package pt.ulisboa.ewp.node.api.ewp.controller.ounits;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.ounits.v2.OrganizationalUnitsV2;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.ounits.OrganizationalUnitsV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOrganizationalUnitsManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiOrganizationalUnitsManifestEntryProvider(HostPluginManager hostPluginManager,
      ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(OrganizationalUnitsV2HostProvider.class,
        this::getManifestEntryForV2);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV2(String heiId, String baseUrl,
      Collection<OrganizationalUnitsV2HostProvider> hostProviders) {
    OrganizationalUnitsV2 manifestEntry = new OrganizationalUnitsV2();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiOrganizationalUnitsV2Controller.BASE_PATH);

    int maxOunitIdsPerRequest = hostProviders.stream().mapToInt(
            OrganizationalUnitsV2HostProvider::getMaxOunitIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOunitIds(BigInteger.valueOf(maxOunitIdsPerRequest));
    
    int maxOunitCodesPerRequest = hostProviders.stream().mapToInt(
            OrganizationalUnitsV2HostProvider::getMaxOunitCodesPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxOunitCodes(BigInteger.valueOf(maxOunitCodesPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
