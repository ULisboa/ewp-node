package pt.ulisboa.ewp.node.api.ewp.controller.courses;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.courses.v0.CoursesV0;
import java.math.BigInteger;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.CoursesV0HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Service
public class EwpApiCoursesManifestEntryProvider extends EwpManifestEntryProvider {

  public EwpApiCoursesManifestEntryProvider(HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(CoursesV0HostProvider.class,
        this::getManifestEntryForV0);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV0(String heiId,
      String baseUrl, CoursesV0HostProvider hostProvider) {
    CoursesV0 manifestEntry = new CoursesV0();
    manifestEntry.setVersion(hostProvider.getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiCoursesV0Controller.BASE_PATH);
    manifestEntry.setMaxLosIds(BigInteger.valueOf(hostProvider.getMaxLosIdsPerRequest()));
    manifestEntry.setMaxLosCodes(BigInteger.valueOf(hostProvider.getMaxLosCodesPerRequest()));
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }

}
