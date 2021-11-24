package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.las.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.ManifestApiEntryBaseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrV1;
import java.util.Collection;
import org.springframework.stereotype.Component;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.OutgoingMobilityLearningAgreementsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpManifestEntryProvider;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@Component
public class EwpApiOutgoingMobilityLearningAgreementCnrManifestEntryProvider
    extends EwpManifestEntryProvider {

  public EwpApiOutgoingMobilityLearningAgreementCnrManifestEntryProvider(
      HostPluginManager hostPluginManager) {
    super(hostPluginManager);

    super.registerHostProviderToManifestEntryConverter(
        OutgoingMobilityLearningAgreementsV1HostProvider.class,
        this::getManifestEntryForV1);
  }

  public ManifestApiEntryBaseV1 getManifestEntryForV1(String heiId, String baseUrl,
      Collection<OutgoingMobilityLearningAgreementsV1HostProvider> hostProviders) {
    OmobilityLaCnrV1 manifestEntry = new OmobilityLaCnrV1();
    manifestEntry.setVersion(hostProviders.iterator().next().getVersion());
    manifestEntry.setAdminNotes(null);
    manifestEntry.setUrl(
        baseUrl + EwpApiOutgoingMobilityLearningAgreementCnrV1Controller.BASE_PATH);
    manifestEntry.setHttpSecurity(getDefaultHttpSecurityOptions());
    return manifestEntry;
  }
}
