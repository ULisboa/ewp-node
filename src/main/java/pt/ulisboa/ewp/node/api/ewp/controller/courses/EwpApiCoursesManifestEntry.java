package pt.ulisboa.ewp.node.api.ewp.controller.courses;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.courses.v0.CoursesV0;
import java.math.BigInteger;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.CoursesHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiCoursesManifestEntry
    implements EwpApiManifestEntryWithHttpSecurityOptionsStrategy {

  private final HostPluginManager hostPluginManager;

  public EwpApiCoursesManifestEntry(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @Override
  public Optional<ManifestApiEntryBaseV1> getManifestEntry(String heiId, String baseUrl) {
    Optional<CoursesHostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, CoursesHostProvider.class);
    if (providerOptional.isEmpty()) {
      return Optional.empty();
    }
    CoursesHostProvider provider = providerOptional.get();

    CoursesV0 manifestEntry = new CoursesV0();
    manifestEntry.setVersion(EwpApiConstants.COURSES_VERSION);
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + "courses");
    manifestEntry.setMaxLosIds(BigInteger.valueOf(provider.getMaxLosIdsPerRequest()));
    manifestEntry.setMaxLosCodes(BigInteger.valueOf(provider.getMaxLosCodesPerRequest()));
    manifestEntry.setHttpSecurity(getHttpSecurityOptions());
    return Optional.of(manifestEntry);
  }
}
