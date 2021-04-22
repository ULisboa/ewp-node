package pt.ulisboa.ewp.node.api.ewp.controller.courses.replication;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.courses.replication.v1.SimpleCourseReplicationV1;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.SimpleCourseReplicationHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiSimpleCourseReplicationManifestEntry
    implements EwpApiManifestEntryWithHttpSecurityOptionsStrategy {

  private final HostPluginManager hostPluginManager;

  public EwpApiSimpleCourseReplicationManifestEntry(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @Override
  public Optional<ManifestApiEntryBaseV1> getManifestEntry(String heiId, String baseUrl) {
    Optional<SimpleCourseReplicationHostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, SimpleCourseReplicationHostProvider.class);
    if (providerOptional.isEmpty()) {
      return Optional.empty();
    }

    SimpleCourseReplicationV1 manifestEntry = new SimpleCourseReplicationV1();
    manifestEntry.setVersion(EwpApiConstants.SIMPLE_COURSE_REPLICATION_VERSION);
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(baseUrl + "courses/replication");
    manifestEntry.setAllowsAnonymousAccess(false);
    manifestEntry.setSupportsModifiedSince(false);
    manifestEntry.setHttpSecurity(getHttpSecurityOptions());
    return Optional.of(manifestEntry);
  }
}
