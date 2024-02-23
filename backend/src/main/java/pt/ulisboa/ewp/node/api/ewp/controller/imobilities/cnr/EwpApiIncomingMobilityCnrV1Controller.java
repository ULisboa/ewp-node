package pt.ulisboa.ewp.node.api.ewp.controller.imobilities.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.imobilities.cnr.v1.ImobilityCnrResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.cnr.IncomingMobilityCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiIncomingMobilityCnrV1Controller.BASE_PATH)
public class EwpApiIncomingMobilityCnrV1Controller {

  public static final String BASE_PATH = "imobilities/cnr/v1";

  private final HostPluginManager hostPluginManager;

  public EwpApiIncomingMobilityCnrV1Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @EwpApiEndpoint(api = "imobility-cnr", apiMajorVersion = 1)
  @RequestMapping(
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Incoming Mobility CNR API.",
      tags = {"ewp"})
  public ResponseEntity<ImobilityCnrResponseV1> incomingMobilityCnr(
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID) String receivingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> omobilityIds) {

    Collection<IncomingMobilityCnrV1HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        IncomingMobilityCnrV1HostProvider.class);
    for (IncomingMobilityCnrV1HostProvider provider : providers) {
      provider.onChangeNotification(receivingHeiId, omobilityIds);
    }

    return ResponseEntity.ok(new ImobilityCnrResponseV1(new EmptyV1()));
  }
}
