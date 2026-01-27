package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.omobilities.cnr.v2.OmobilityCnrResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
import java.util.Collection;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.cnr.OutgoingMobilityCnrV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.config.registry.RegistryProperties;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilityCnrV2Controller.BASE_PATH)
public class EwpApiOutgoingMobilityCnrV2Controller {

  public static final String BASE_PATH = "omobilities/cnr/v2";

  private final HostPluginManager hostPluginManager;
  private final RegistryProperties registryProperties;

  public EwpApiOutgoingMobilityCnrV2Controller(
      HostPluginManager hostPluginManager, RegistryProperties registryProperties) {
    this.hostPluginManager = hostPluginManager;
    this.registryProperties = registryProperties;
  }

  @EwpApiEndpoint(api = "omobility-cnr", apiMajorVersion = 2)
  @RequestMapping(
      path = "/{heiId}",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobility CNR API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilityCnrResponseV2> outgoingMobilityCnr(
      EwpApiHostAuthenticationToken authenticationToken,
      @NotNull @PathVariable String heiId,
      @NotNull @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> omobilityIds) {

    String requesterCoveredHeiId =
        authenticationToken.getPrincipal().getHeiIdsCoveredByClient().iterator().next();

    // Note: IF request came from the EWP registry's validator,
    // then do not propagate the CNR requests, as they do not exist in reality.
    if (requesterCoveredHeiId.matches(this.registryProperties.getValidatorHeiIdsRegex())) {
      return ResponseEntity.ok(new OmobilityCnrResponseV2(new EmptyV1()));
    }

    Collection<OutgoingMobilityCnrV2HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(heiId, OutgoingMobilityCnrV2HostProvider.class);
    for (OutgoingMobilityCnrV2HostProvider provider : providers) {
      provider.onChangeNotification(requesterCoveredHeiId, omobilityIds);
    }

    return ResponseEntity.ok(new OmobilityCnrResponseV2(new EmptyV1()));
  }
}
