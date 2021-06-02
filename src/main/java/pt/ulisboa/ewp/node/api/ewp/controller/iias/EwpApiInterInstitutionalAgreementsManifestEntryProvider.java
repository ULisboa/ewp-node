package pt.ulisboa.ewp.node.api.ewp.controller.iias;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.iias.v4.IiasV4;
import java.math.BigInteger;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV4HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiInterInstitutionalAgreementsManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiInterInstitutionalAgreementsManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(
        InterInstitutionalAgreementsV4HostProvider.class,
        this::getManifestEntryForV4);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV4(String heiId, String baseUrl,
      InterInstitutionalAgreementsV4HostProvider hostProvider) {
    IiasV4 manifestEntry = new IiasV4();
    manifestEntry.setVersion(hostProvider.getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry
        .setIndexUrl(baseUrl + EwpApiInterInstitutionalAgreementsV4Controller.BASE_PATH + "/index");
    manifestEntry
        .setGetUrl(baseUrl + EwpApiInterInstitutionalAgreementsV4Controller.BASE_PATH + "/get");
    manifestEntry.setMaxIiaIds(BigInteger.valueOf(hostProvider.getMaxIiaIdsPerRequest()));
    manifestEntry.setMaxIiaCodes(BigInteger.valueOf(hostProvider.getMaxIiaCodesPerRequest()));
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
