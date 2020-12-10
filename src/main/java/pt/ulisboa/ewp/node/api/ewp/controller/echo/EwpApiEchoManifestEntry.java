package pt.ulisboa.ewp.node.api.ewp.controller.echo;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.echo.v2.EchoV2;
import java.util.Optional;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiManifestEntryWithHttpSecurityOptionsStrategy;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;

@Component
public class EwpApiEchoManifestEntry extends EwpApiManifestEntryWithHttpSecurityOptionsStrategy {
  @Override
  public Optional<ManifestApiEntryBaseV1> getManifestEntry(String heiId, String baseUrl) {
    EchoV2 echo = new EchoV2();
    echo.setVersion(EwpApiConstants.ECHO_VERSION);
    echo.setAdminNotes(null);
    echo.setUrl(baseUrl + "echo");
    echo.setHttpSecurity(getHttpSecurityOptions());

    return Optional.of(echo);
  }
}
