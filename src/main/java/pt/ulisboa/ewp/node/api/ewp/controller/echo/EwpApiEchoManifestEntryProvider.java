package pt.ulisboa.ewp.node.api.ewp.controller.echo;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.echo.v2.EchoV2;
import java.util.Collection;
import java.util.Collections;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiEchoManifestEntryProvider extends EwpManifestEntryProvider {

  public EwpApiEchoManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);
  }

  @Override
  public Collection<ManifestApiEntryBaseV1> getExtraManifestEntries(String heiId, String baseUrl) {
    EchoV2 echo = new EchoV2();
    echo.setVersion(EwpApiConstants.ECHO_VERSION);
    echo.setAdminNotes(null);
    echo.setUrl(baseUrl + "echo");
    echo.setHttpSecurity(getDefaultHttpSecurityOptions());
    return Collections.singletonList(echo);
  }
}
