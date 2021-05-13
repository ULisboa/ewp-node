package pt.ulisboa.ewp.node.api.ewp.controller.factsheets;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetV1;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.FactSheetsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiFactSheetsManifestEntry
    implements EwpApiManifestEntryWithHttpSecurityOptionsStrategy {

  private final HostPluginManager hostPluginManager;

  public EwpApiFactSheetsManifestEntry(
      HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @Override
  public Optional<ManifestApiEntryBaseV1> getManifestEntry(String heiId, String baseUrl) {
    Optional<FactSheetsHostProvider> providerOptional =
        hostPluginManager
            .getProvider(heiId, FactSheetsHostProvider.class);
    if (providerOptional.isEmpty()) {
      return Optional.empty();
    }

    FactsheetV1 manifestEntry = new FactsheetV1();
    manifestEntry.setVersion(EwpApiConstants.FACT_SHEETS_VERSION);
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + "factsheets");
    manifestEntry.setMaxHeiIds(BigInteger.valueOf(EwpApiConstants.MAX_HEI_IDS));
    manifestEntry.setHttpSecurity(getHttpSecurityOptions());
    return Optional.of(manifestEntry);
  }
}
