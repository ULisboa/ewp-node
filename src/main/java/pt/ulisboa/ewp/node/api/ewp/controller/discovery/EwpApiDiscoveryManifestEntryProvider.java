package pt.ulisboa.ewp.node.api.ewp.controller.discovery;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.discovery.v5.DiscoveryV5;
import java.util.Collection;
import java.util.Collections;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiDiscoveryManifestEntryProvider extends EwpManifestEntryProvider {

  public EwpApiDiscoveryManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);
  }

  @Override
  public Collection<ManifestApiEntryBaseV1> getExtraManifestEntries(String heiId, String baseUrl) {
    DiscoveryV5 discovery = new DiscoveryV5();
    discovery.setVersion(EwpApiConstants.DISCOVERY_VERSION);
    discovery.setAdminNotes(null);
    discovery.setUrl(baseUrl + "manifest");
    return Collections.singletonList(discovery);
  }
}
