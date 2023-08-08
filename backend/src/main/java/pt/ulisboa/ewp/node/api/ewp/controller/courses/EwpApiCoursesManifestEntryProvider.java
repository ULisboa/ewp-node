package pt.ulisboa.ewp.node.api.ewp.controller.courses;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.courses.v0.CoursesV0;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.CoursesV0HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.config.manifest.ManifestProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Service
public class EwpApiCoursesManifestEntryProvider extends EwpManifestEntryProvider {

  public EwpApiCoursesManifestEntryProvider(HostPluginManager hostPluginManager,
      ManifestProperties manifestProperties) {
    super(hostPluginManager, manifestProperties);

    super.registerHostProviderToManifestEntryConverter(CoursesV0HostProvider.class,
        this::getManifestEntryForV0);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV0(String heiId,
      String baseUrl, Collection<CoursesV0HostProvider> hostProviders) {
    CoursesV0 manifestEntry = new CoursesV0();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiCoursesV0Controller.BASE_PATH);

    int maxLosIdsPerRequest = hostProviders.stream().mapToInt(
            CoursesV0HostProvider::getMaxLosIdsPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxLosIds(BigInteger.valueOf(maxLosIdsPerRequest));

    int maxLosCodesPerRequest = hostProviders.stream().mapToInt(
            CoursesV0HostProvider::getMaxLosCodesPerRequest)
        .min().orElse(0);
    manifestEntry.setMaxLosCodes(BigInteger.valueOf(maxLosCodesPerRequest));

    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }

}
