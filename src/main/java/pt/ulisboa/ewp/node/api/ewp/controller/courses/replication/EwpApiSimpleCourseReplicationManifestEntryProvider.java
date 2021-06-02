package pt.ulisboa.ewp.node.api.ewp.controller.courses.replication;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.courses.replication.v1.SimpleCourseReplicationV1;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.replication.SimpleCourseReplicationV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiSimpleCourseReplicationManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiSimpleCourseReplicationManifestEntryProvider(HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(SimpleCourseReplicationV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      SimpleCourseReplicationV1HostProvider hostProvider) {
    SimpleCourseReplicationV1 manifestEntry = new SimpleCourseReplicationV1();
    manifestEntry.setVersion(hostProvider.getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + EwpApiSimpleCourseReplicationV1Controller.BASE_PATH);
    manifestEntry.setAllowsAnonymousAccess(false);
    manifestEntry.setSupportsModifiedSince(false);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
