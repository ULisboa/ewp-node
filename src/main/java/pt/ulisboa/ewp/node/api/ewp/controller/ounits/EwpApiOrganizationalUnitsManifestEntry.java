package pt.ulisboa.ewp.node.api.ewp.controller.ounits;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.ounits.v2.OrganizationalUnitsV2;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.OrganizationalUnitsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOrganizationalUnitsManifestEntry
    extends EwpApiManifestEntryWithHttpSecurityOptionsStrategy {

  private final HostPluginManager hostPluginManager;

  public EwpApiOrganizationalUnitsManifestEntry(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @Override
  public Optional<ManifestApiEntryBaseV1> getManifestEntry(String heiId, String baseUrl) {
    Optional<OrganizationalUnitsHostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, OrganizationalUnitsHostProvider.class);
    if (providerOptional.isEmpty()) {
      return Optional.empty();
    }
    OrganizationalUnitsHostProvider provider = providerOptional.get();

    OrganizationalUnitsV2 manifestEntry = new OrganizationalUnitsV2();
    manifestEntry.setVersion(EwpApiConstants.ORGANIZATIONAL_UNITS_VERSION);
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + "ounits");
    manifestEntry.setMaxOunitIds(BigInteger.valueOf(provider.getMaxOunitIdsPerRequest()));
    manifestEntry.setMaxOunitCodes(BigInteger.valueOf(provider.getMaxOunitCodesPerRequest()));
    manifestEntry.setHttpSecurity(getHttpSecurityOptions());
    return Optional.of(manifestEntry);
  }
}
