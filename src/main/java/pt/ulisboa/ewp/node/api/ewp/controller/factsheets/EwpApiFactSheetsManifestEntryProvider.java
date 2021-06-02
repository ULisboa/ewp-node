package pt.ulisboa.ewp.node.api.ewp.controller.factsheets;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetV1;
import java.math.BigInteger;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.factsheets.FactSheetsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiFactSheetsManifestEntryProvider extends EwpManifestEntryProvider {

  public EwpApiFactSheetsManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(FactSheetsV1HostProvider.class,
        this::getManifestEntry);
  }

  public ManifestApiEntryBaseV1 getManifestEntry(String heiId, String baseUrl,
      FactSheetsV1HostProvider hostProvider) {
    FactsheetV1 manifestEntry = new FactsheetV1();
    manifestEntry.setVersion(hostProvider.getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiFactSheetsV1Controller.BASE_PATH);
    manifestEntry.setMaxHeiIds(BigInteger.valueOf(EwpApiConstants.MAX_HEI_IDS));
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
