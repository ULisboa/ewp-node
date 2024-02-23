package pt.ulisboa.ewp.node.api.ewp.controller.iias.approvals.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.iias.approval.cnr.v2.IiaApprovalCnrResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.cnr.InterInstitutionalAgreementApprovalCnrV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI
        + EwpApiInterInstitutionalAgreementApprovalCnrV2Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementApprovalCnrV2Controller {

  public static final String BASE_PATH = "iias/approvals/cnr/v2";

  private final HostPluginManager hostPluginManager;

  public EwpApiInterInstitutionalAgreementApprovalCnrV2Controller(
      HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @EwpApiEndpoint(api = "iia-approval-cnr", apiMajorVersion = 2)
  @RequestMapping(
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIA Approval CNR API.",
      tags = {"ewp"})
  public ResponseEntity<IiaApprovalCnrResponseV2> iiaApprovalCnr(
      EwpApiHostAuthenticationToken authenticationToken,
      @RequestParam(value = EwpApiParamConstants.OWNER_HEI_ID) String ownerHeiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID) String iiaId) {

    String approvingHeiId =
        authenticationToken.getPrincipal().getHeiIdsCoveredByClient().iterator().next();

    Collection<InterInstitutionalAgreementApprovalCnrV2HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(
            InterInstitutionalAgreementApprovalCnrV2HostProvider.class);
    for (InterInstitutionalAgreementApprovalCnrV2HostProvider provider : providers) {
      provider.onChangeNotification(approvingHeiId, ownerHeiId, iiaId);
    }

    return ResponseEntity.ok(new IiaApprovalCnrResponseV2(new EmptyV1()));
  }
}
