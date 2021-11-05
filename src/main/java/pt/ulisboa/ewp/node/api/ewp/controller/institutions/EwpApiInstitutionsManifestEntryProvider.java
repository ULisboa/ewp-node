package pt.ulisboa.ewp.node.api.ewp.controller.institutions;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsV2;
import java.math.BigInteger;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.institutions.InstitutionsV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInstitutionsManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiInstitutionsManifestEntryProvider(HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(InstitutionsV2HostProvider.class,
        this::getManifestEntryForV2);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV2(String heiId, String baseUrl,
      Collection<InstitutionsV2HostProvider> hostProviders) {
    InstitutionsV2 institutions = new InstitutionsV2();
    institutions.setVersion(hostProviders.iterator().next().getVersion());
    institutions.setAdminNotes(null);
    institutions.setUrl(baseUrl + EwpApiInstitutionsV2Controller.BASE_PATH);
    institutions.setMaxHeiIds(BigInteger.valueOf(EwpApiConstants.MAX_HEI_IDS));
    institutions.setHttpSecurity(getDefaultHttpSecurityOptions());
    return institutions;
  }
}
