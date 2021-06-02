package pt.ulisboa.ewp.node.api.ewp.controller.ounits;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.ounits.v2.OrganizationalUnitsV2;
import java.math.BigInteger;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.ounits.OrganizationalUnitsV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOrganizationalUnitsManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiOrganizationalUnitsManifestEntryProvider(HostPluginManager hostPluginManager) {
    super(hostPluginManager);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV2(String heiId, String baseUrl,
      OrganizationalUnitsV2HostProvider hostProvider) {
    OrganizationalUnitsV2 manifestEntry = new OrganizationalUnitsV2();
    manifestEntry.setVersion(hostProvider.getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiOrganizationalUnitsV2Controller.BASE_PATH);
    manifestEntry.setMaxOunitIds(BigInteger.valueOf(hostProvider.getMaxOunitIdsPerRequest()));
    manifestEntry.setMaxOunitCodes(BigInteger.valueOf(hostProvider.getMaxOunitCodesPerRequest()));
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
