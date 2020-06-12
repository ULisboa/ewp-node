package pt.ulisboa.ewp.node.api.ewp.controller.institutions;

import eu.erasmuswithoutpaper.api.architecture.ManifestApiEntryBase;
import eu.erasmuswithoutpaper.api.institutions.Institutions;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.InstitutionsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInstitutionsManifestEntry
    extends EwpApiManifestEntryWithHttpSecurityOptionsStrategy {

  private final HostPluginManager hostPluginManager;

  public EwpApiInstitutionsManifestEntry(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @Override
  public Optional<ManifestApiEntryBase> getManifestEntry(String heiId, String baseUrl) {
    Optional<InstitutionsHostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, InstitutionsHostProvider.class);
    if (providerOptional.isEmpty()) {
      return Optional.empty();
    }
    InstitutionsHostProvider provider = providerOptional.get();

    Institutions institutions = new Institutions();
    institutions.setVersion(EwpApiConstants.INSTITUTIONS_VERSION);
    institutions.setAdminNotes(null);
    institutions.setUrl(baseUrl + EwpApiConstants.API_BASE_URI + "institutions");
    institutions.setMaxHeiIds(BigInteger.valueOf(EwpApiConstants.MAX_HEI_IDS));
    institutions.setHttpSecurity(getHttpSecurityOptions());
    return Optional.of(institutions);
  }
}
