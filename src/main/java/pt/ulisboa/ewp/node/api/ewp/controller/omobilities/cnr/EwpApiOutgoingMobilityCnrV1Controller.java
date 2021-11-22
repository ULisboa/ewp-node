package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.omobilities.cnr.v1.OmobilityCnrResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.cnr.OutgoingMobilityCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilityCnrV1Controller.BASE_PATH)
public class EwpApiOutgoingMobilityCnrV1Controller {

  public static final String BASE_PATH = "omobilities/cnr/v1";

  private final HostPluginManager hostPluginManager;

  public EwpApiOutgoingMobilityCnrV1Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @RequestMapping(method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs CNR API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilityCnrResponseV1> iiaCnr(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID) String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> omobilityIds) {

    Collection<OutgoingMobilityCnrV1HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        OutgoingMobilityCnrV1HostProvider.class);
    for (OutgoingMobilityCnrV1HostProvider provider : providers) {
      provider.onChangeNotification(sendingHeiId, omobilityIds);
    }

    return ResponseEntity.ok(new OmobilityCnrResponseV1(new EmptyV1()));
  }
}
