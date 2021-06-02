package pt.ulisboa.ewp.node.api.ewp.controller.iias.approval;

import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.InterInstitutionalAgreementsApprovalV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsApprovalV1Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementsApprovalV1Controller {

  public static final String BASE_PATH = "iias/approval/v1";

  private final HostPluginManager hostPluginManager;

  public EwpApiInterInstitutionalAgreementsApprovalV1Controller(
      HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Approval API.",
      tags = {"ewp"})
  public ResponseEntity<IiasApprovalResponseV1> iiasApprovalGet(
      @RequestParam(value = EwpApiParamConstants.APPROVING_HEI_ID, defaultValue = "") String approvingHeiId,
      @RequestParam(value = EwpApiParamConstants.OWNER_HEI_ID, defaultValue = "") String ownerHeiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID) Collection<String> iiaIds,
      @RequestParam(value = EwpApiParamConstants.SEND_PDF, required = false)
          Boolean sendPdf) {

    return ResponseEntity.ok(getIiasApprovals(approvingHeiId, ownerHeiId, iiaIds, sendPdf));
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Approval API.",
      tags = {"ewp"})
  public ResponseEntity<IiasApprovalResponseV1> iiasApprovalPost(
      @RequestParam(value = EwpApiParamConstants.APPROVING_HEI_ID, defaultValue = "") String approvingHeiId,
      @RequestParam(value = EwpApiParamConstants.OWNER_HEI_ID, defaultValue = "") String ownerHeiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID) Collection<String> iiaIds,
      @RequestParam(value = EwpApiParamConstants.SEND_PDF, required = false)
          Boolean sendPdf) {

    return ResponseEntity.ok(getIiasApprovals(approvingHeiId, ownerHeiId, iiaIds, sendPdf));
  }

  private IiasApprovalResponseV1 getIiasApprovals(String approvingHeiId, String ownerHeiId,
      Collection<String> iiaIds, Boolean sendPdf) {
    Collection<IiasApprovalResponseV1.Approval> iiaApprovals = getHostProvider(
        approvingHeiId)
        .findByIiaIds(approvingHeiId, ownerHeiId, iiaIds, sendPdf);
    IiasApprovalResponseV1 response = new IiasApprovalResponseV1();
    response.getApproval().addAll(iiaApprovals);
    return response;
  }

  private InterInstitutionalAgreementsApprovalV1HostProvider getHostProvider(String heiId) {
    Optional<InterInstitutionalAgreementsApprovalV1HostProvider> providerOptional =
        hostPluginManager
            .getProvider(heiId, InterInstitutionalAgreementsApprovalV1HostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }
    return providerOptional.get();
  }
}
