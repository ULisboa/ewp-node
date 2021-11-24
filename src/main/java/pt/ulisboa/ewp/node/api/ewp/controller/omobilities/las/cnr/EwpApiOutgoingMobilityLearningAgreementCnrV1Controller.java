package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.las.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.cnr.OutgoingMobilityLearningAgreementCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilityLearningAgreementCnrV1Controller.BASE_PATH)
public class EwpApiOutgoingMobilityLearningAgreementCnrV1Controller {

  public static final String BASE_PATH = "omobilities/las/cnr/v1";

  private final HostPluginManager hostPluginManager;

  public EwpApiOutgoingMobilityLearningAgreementCnrV1Controller(
      HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @RequestMapping(method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobility Learning Agreement CNR API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilityLaCnrResponseV1> outgoingMobilityLearningAgreementCnr(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID) String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> omobilityIds) {

    Collection<OutgoingMobilityLearningAgreementCnrV1HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        OutgoingMobilityLearningAgreementCnrV1HostProvider.class);
    for (OutgoingMobilityLearningAgreementCnrV1HostProvider provider : providers) {
      provider.onChangeNotification(sendingHeiId, omobilityIds);
    }

    return ResponseEntity.ok(new OmobilityLaCnrResponseV1(new EmptyV1()));
  }
}
