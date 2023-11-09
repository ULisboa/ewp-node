package pt.ulisboa.ewp.node.api.ewp.controller.iias.approvals;

import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalResponseV1;
import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalResponseV1.Approval;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.InterInstitutionalAgreementsApprovalV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownOrganizationalUnitIdException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsApprovalV1Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementsApprovalV1Controller {

  public static final String BASE_PATH = "iias/approval/v1";

  private final HostPluginManager hostPluginManager;

  private final EwpInterInstitutionalAgreementMappingRepository mappingRepository;

  public EwpApiInterInstitutionalAgreementsApprovalV1Controller(
      HostPluginManager hostPluginManager,
      EwpInterInstitutionalAgreementMappingRepository mappingRepository) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
  }

  @RequestMapping(method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Approval API.",
      tags = {"ewp"})
  public ResponseEntity<IiasApprovalResponseV1> iiasApproval(
      EwpApiHostAuthenticationToken authenticationToken,
      @RequestParam(value = EwpApiParamConstants.APPROVING_HEI_ID, defaultValue = "") String approvingHeiId,
      @RequestParam(value = EwpApiParamConstants.OWNER_HEI_ID, defaultValue = "") String ownerHeiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID) Collection<String> iiaIds,
      @RequestParam(value = EwpApiParamConstants.SEND_PDF, required = false)
          Boolean sendPdf) {

    if (!authenticationToken.getPrincipal().getHeiIdsCoveredByClient().contains(ownerHeiId)) {
      throw new EwpBadRequestException("Owner HEI ID does not match requester covered HEI ID");
    }

    if (!hostPluginManager.hasHostProvider(approvingHeiId,
        InterInstitutionalAgreementsApprovalV1HostProvider.class)) {
      throw new EwpUnknownHeiIdException(ownerHeiId);
    }

    IiasApprovalResponseV1 response = new IiasApprovalResponseV1();

    // NOTE: The algorithm handles each IIA ID individually as it may be necessary to fall back to
    // one or more providers.
    for (String iiaId : iiaIds) {
      List<InterInstitutionalAgreementsApprovalV1HostProvider> providersChain = getProvidersChainForHeiAndIiaId(
          approvingHeiId, iiaId);
      for (InterInstitutionalAgreementsApprovalV1HostProvider possibleProvider : providersChain) {
        Collection<Approval> providerResponse =
            possibleProvider.findByIiaIds(approvingHeiId, ownerHeiId, List.of(iiaId), sendPdf);
        if (!providerResponse.isEmpty()) {
          Approval approval = providerResponse.iterator().next();
          response.getApproval().add(approval);
          break;
        }
      }
    }

    return ResponseEntity.ok(response);
  }

  private List<InterInstitutionalAgreementsApprovalV1HostProvider> getProvidersChainForHeiAndIiaId(
      String heiId, String iiaId) throws EwpUnknownHeiIdException {

    if (!hostPluginManager.hasHostProvider(
        heiId, InterInstitutionalAgreementsApprovalV1HostProvider.class)) {
      return new ArrayList<>();
    }

    Optional<EwpInterInstitutionalAgreementMapping> mappingOptional =
        mappingRepository.findByHeiIdAndIiaId(heiId, iiaId);
    if (mappingOptional.isPresent()) {
      EwpInterInstitutionalAgreementMapping mapping = mappingOptional.get();
      Optional<InterInstitutionalAgreementsApprovalV1HostProvider> providerOptional =
          hostPluginManager.getSingleProvider(
              heiId, mapping.getOunitId(), InterInstitutionalAgreementsApprovalV1HostProvider.class);
      if (providerOptional.isPresent()) {
        InterInstitutionalAgreementsApprovalV1HostProvider provider = providerOptional.get();
        return List.of(provider);
      } else {
        throw new EwpUnknownOrganizationalUnitIdException(heiId, mapping.getOunitId());
      }

    } else {
      return hostPluginManager.getPrimaryFollowedByNonPrimaryProviders(
          heiId, InterInstitutionalAgreementsApprovalV1HostProvider.class);
    }
  }
}
