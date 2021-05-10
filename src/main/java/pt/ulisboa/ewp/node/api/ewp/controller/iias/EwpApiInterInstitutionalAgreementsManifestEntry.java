package pt.ulisboa.ewp.node.api.ewp.controller.iias;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.iias.v4.IiasV4;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.InterInstitutionalAgreementsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInterInstitutionalAgreementsManifestEntry
    implements EwpApiManifestEntryWithHttpSecurityOptionsStrategy {

  private final HostPluginManager hostPluginManager;

  public EwpApiInterInstitutionalAgreementsManifestEntry(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @Override
  public Optional<ManifestApiEntryBaseV1> getManifestEntry(String heiId, String baseUrl) {
    Optional<InterInstitutionalAgreementsHostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, InterInstitutionalAgreementsHostProvider.class);
    if (providerOptional.isEmpty()) {
      return Optional.empty();
    }
    InterInstitutionalAgreementsHostProvider hostProvider = providerOptional
        .get();

    IiasV4 manifestEntry = new IiasV4();
    manifestEntry.setVersion(EwpApiConstants.INTER_INSTITUTIONAL_AGREEMENTS_VERSION);
    manifestEntry.setAdminNotes(null);
    manifestEntry.setIndexUrl(baseUrl + "iias/index");
    manifestEntry.setGetUrl(baseUrl + "iias/get");
    manifestEntry.setMaxIiaIds(BigInteger.valueOf(hostProvider.getMaxIiaIdsPerRequest()));
    manifestEntry.setMaxIiaCodes(BigInteger.valueOf(hostProvider.getMaxIiaCodesPerRequest()));
    manifestEntry.setHttpSecurity(getHttpSecurityOptions());
    return Optional.of(manifestEntry);
  }
}
