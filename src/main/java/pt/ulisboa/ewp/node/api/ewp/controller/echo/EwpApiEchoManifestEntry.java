package pt.ulisboa.ewp.node.api.ewp.controller.echo;

import eu.erasmuswithoutpaper.api.architecture.ManifestApiEntryBase;
import eu.erasmuswithoutpaper.api.echo.Echo;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;

@Component
public class EwpApiEchoManifestEntry extends EwpApiManifestEntryWithHttpSecurityOptionsStrategy {
  @Override
  public Optional<ManifestApiEntryBase> getManifestEntry(String heiId, String baseUrl) {
    Echo echo = new Echo();
    echo.setVersion(EwpApiConstants.ECHO_VERSION);
    echo.setAdminNotes(null);
    echo.setUrl(baseUrl + EwpApiConstants.API_BASE_URI + "echo");
    echo.setHttpSecurity(getHttpSecurityOptions());

    return Optional.of(echo);
  }
}
