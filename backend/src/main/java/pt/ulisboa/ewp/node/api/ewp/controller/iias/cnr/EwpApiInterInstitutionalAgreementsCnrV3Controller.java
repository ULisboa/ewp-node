package pt.ulisboa.ewp.node.api.ewp.controller.iias.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.iias.cnr.v3.IiaCnrResponseV3;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import javax.validation.constraints.NotNull;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.cnr.InterInstitutionalAgreementCnrV3HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsCnrV3Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementsCnrV3Controller {

  public static final String BASE_PATH = "iias/cnr/v3";

  private final HostPluginManager hostPluginManager;

  public EwpApiInterInstitutionalAgreementsCnrV3Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @RequestMapping(
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs CNR API.",
      tags = {"ewp"})
  public ResponseEntity<IiaCnrResponseV3> iiaCnr(
      EwpApiHostAuthenticationToken authenticationToken,
      @NotNull @RequestParam(value = EwpApiParamConstants.HEI_ID) String heiId,
      @NotNull @RequestParam(value = EwpApiParamConstants.IIA_ID) String iiaId) {

    String requesterCoveredHeiId = authenticationToken.getPrincipal().getHeiIdsCoveredByClient().iterator().next();

    Collection<InterInstitutionalAgreementCnrV3HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(heiId, InterInstitutionalAgreementCnrV3HostProvider.class);
    for (InterInstitutionalAgreementCnrV3HostProvider provider : providers) {
      provider.onChangeNotification(requesterCoveredHeiId, iiaId);
    }

    return ResponseEntity.ok(new IiaCnrResponseV3(new EmptyV1()));
  }
}
