package pt.ulisboa.ewp.node.api.ewp.controller.iias.approvals.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.iias.approval.cnr.v1.IiaApprovalCnrResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.cnr.InterInstitutionalAgreementApprovalCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI
        + EwpApiInterInstitutionalAgreementApprovalCnrV1Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementApprovalCnrV1Controller {

  public static final String BASE_PATH = "iias/approvals/cnr/v1";

  private final HostPluginManager hostPluginManager;

  public EwpApiInterInstitutionalAgreementApprovalCnrV1Controller(
      HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @EwpApiEndpoint(api = "iia-approval-cnr", apiMajorVersion = 1)
  @RequestMapping(
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIA Approval CNR API.",
      tags = {"ewp"})
  public ResponseEntity<IiaApprovalCnrResponseV1> iiaApprovalCnr(
      @RequestParam(value = EwpApiParamConstants.APPROVING_HEI_ID) String approvingHeiId,
      @RequestParam(value = EwpApiParamConstants.OWNER_HEI_ID) String ownerHeiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID) String iiaId) {

    Collection<InterInstitutionalAgreementApprovalCnrV1HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        InterInstitutionalAgreementApprovalCnrV1HostProvider.class);
    for (InterInstitutionalAgreementApprovalCnrV1HostProvider provider : providers) {
      provider.onChangeNotification(approvingHeiId, ownerHeiId, iiaId);
    }

    return ResponseEntity.ok(new IiaApprovalCnrResponseV1(new EmptyV1()));
  }
}
