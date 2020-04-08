package pt.ulisboa.ewp.node.api.ewp.controller.discovery;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Component;

import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import eu.erasmuswithoutpaper.api.architecture.ManifestApiEntryBase;
import eu.erasmuswithoutpaper.api.discovery.Discovery;

@Component
public class EwpApiDiscoveryManifestEntry extends EwpApiManifestEntryStrategy {

  @Override
  public ManifestApiEntryBase getManifestEntry(HttpServletRequest request) {
    Discovery discovery = new Discovery();
    discovery.setVersion(EwpApiConstants.DISCOVERY_VERSION);
    discovery.setAdminNotes(null);
    discovery.setUrl(getBaseUrl(request) + EwpApiConstants.EWP_API_BASE_URI + "manifest");
    return discovery;
  }
}
