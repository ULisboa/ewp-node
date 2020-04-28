package pt.ulisboa.ewp.node.api.ewp.controller.echo;

import eu.erasmuswithoutpaper.api.architecture.ManifestApiEntryBase;
import eu.erasmuswithoutpaper.api.echo.Echo;
import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;

@Component
public class EwpApiEchoManifestEntry extends EwpApiManifestEntryWithHttpSecurityOptionsStrategy {
  @Override
  public ManifestApiEntryBase getManifestEntry(HttpServletRequest request) {
    Echo echo = new Echo();
    echo.setVersion(EwpApiConstants.ECHO_VERSION);
    echo.setAdminNotes(null);
    echo.setUrl(getBaseUrl(request) + EwpApiConstants.API_BASE_URI + "echo");
    echo.setHttpSecurity(getHttpSecurityOptions());

    return echo;
  }
}
