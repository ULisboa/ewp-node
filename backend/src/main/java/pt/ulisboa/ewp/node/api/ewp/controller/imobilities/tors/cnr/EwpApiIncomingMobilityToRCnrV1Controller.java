package pt.ulisboa.ewp.node.api.ewp.controller.imobilities.tors.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.imobilities.tors.cnr.v1.ImobilityTorCnrResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.tors.cnr.IncomingMobilityToRCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.config.registry.RegistryProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiIncomingMobilityToRCnrV1Controller.BASE_PATH)
public class EwpApiIncomingMobilityToRCnrV1Controller {

  public static final String BASE_PATH = "imobilities/tors/cnr/v1";

  private final HostPluginManager hostPluginManager;
  private final RegistryProperties registryProperties;

  public EwpApiIncomingMobilityToRCnrV1Controller(
      HostPluginManager hostPluginManager, RegistryProperties registryProperties) {
    this.hostPluginManager = hostPluginManager;
    this.registryProperties = registryProperties;
  }

  @EwpApiEndpoint(api = "imobility-tor-cnr", apiMajorVersion = 1)
  @RequestMapping(
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Incoming Mobility Transcript Of Records CNR API.",
      tags = {"ewp"})
  public ResponseEntity<ImobilityTorCnrResponseV1> outgoingMobilityLearningAgreementCnr(
      EwpApiHostAuthenticationToken authenticationToken,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID) String receivingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> omobilityIds) {

    String requesterCoveredHeiId =
        authenticationToken.getPrincipal().getHeiIdsCoveredByClient().iterator().next();

    // Note: IF request came from the EWP registry's validator,
    // then do not propagate the CNR requests, as they do not exist in reality.
    if (requesterCoveredHeiId.matches(this.registryProperties.getValidatorHeiIdsRegex())) {
      return ResponseEntity.ok(new ImobilityTorCnrResponseV1(new EmptyV1()));
    }

    Collection<IncomingMobilityToRCnrV1HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        IncomingMobilityToRCnrV1HostProvider.class);
    for (IncomingMobilityToRCnrV1HostProvider provider : providers) {
      provider.onChangeNotification(receivingHeiId, omobilityIds);
    }

    return ResponseEntity.ok(new ImobilityTorCnrResponseV1(new EmptyV1()));
  }
}
