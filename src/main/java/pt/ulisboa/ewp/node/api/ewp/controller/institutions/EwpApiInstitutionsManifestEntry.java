package pt.ulisboa.ewp.node.api.ewp.controller.institutions;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsV2;
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
  public Optional<ManifestApiEntryBaseV1> getManifestEntry(String heiId, String baseUrl) {
    Optional<InstitutionsHostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, InstitutionsHostProvider.class);
    if (providerOptional.isEmpty()) {
      return Optional.empty();
    }
    InstitutionsHostProvider provider = providerOptional.get();

    InstitutionsV2 institutions = new InstitutionsV2();
    institutions.setVersion(EwpApiConstants.INSTITUTIONS_VERSION);
    institutions.setAdminNotes(null);
    institutions.setUrl(baseUrl + "institutions");
    institutions.setMaxHeiIds(BigInteger.valueOf(EwpApiConstants.MAX_HEI_IDS));
    institutions.setHttpSecurity(getHttpSecurityOptions());
    return Optional.of(institutions);
  }
}
