package pt.ulisboa.ewp.node.api.ewp.controller.iias.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.iias.cnr.v2.IiaCnrResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.cnr.InterInstitutionalAgreementCnrV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsCnrV2Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementsCnrV2Controller {

  public static final String BASE_PATH = "iias/cnr/v2";

  private final HostPluginManager hostPluginManager;

  public EwpApiInterInstitutionalAgreementsCnrV2Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @RequestMapping(method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs CNR API.",
      tags = {"ewp"})
  public ResponseEntity<IiaCnrResponseV2> iiaCnr(
      @RequestParam(value = EwpApiParamConstants.NOTIFIER_HEI_ID) String notifierHeiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID) String iiaId) {

    Collection<InterInstitutionalAgreementCnrV2HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        InterInstitutionalAgreementCnrV2HostProvider.class);
    for (InterInstitutionalAgreementCnrV2HostProvider provider : providers) {
      provider.onChangeNotification(notifierHeiId, iiaId);
    }

    return ResponseEntity.ok(new IiaCnrResponseV2(new EmptyV1()));
  }
}
