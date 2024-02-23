package pt.ulisboa.ewp.node.api.ewp.controller.iias.approvals;

import eu.erasmuswithoutpaper.api.iias.approval.v2.IiasApprovalResponseV2;
import eu.erasmuswithoutpaper.api.iias.approval.v2.IiasApprovalResponseV2.Approval;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.InterInstitutionalAgreementsApprovalV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownOrganizationalUnitIdException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsApprovalV2Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementsApprovalV2Controller {

  public static final String BASE_PATH = "iias/approval/v2";

  private final HostPluginManager hostPluginManager;

  private final EwpInterInstitutionalAgreementMappingRepository mappingRepository;

  public EwpApiInterInstitutionalAgreementsApprovalV2Controller(
      HostPluginManager hostPluginManager,
      EwpInterInstitutionalAgreementMappingRepository mappingRepository) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
  }

  @EwpApiEndpoint(api = "iias-approval", apiMajorVersion = 2)
  @RequestMapping(
      path = "/{approvingHeiId}",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Approval API.",
      tags = {"ewp"})
  public ResponseEntity<IiasApprovalResponseV2> iiasApproval(
      EwpApiHostAuthenticationToken authenticationToken,
      @PathVariable String approvingHeiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID) Collection<String> iiaIds) {

    String requesterCoveredHeiId =
        authenticationToken.getPrincipal().getHeiIdsCoveredByClient().iterator().next();

    if (!hostPluginManager.hasHostProvider(
        approvingHeiId, InterInstitutionalAgreementsApprovalV2HostProvider.class)) {
      throw new EwpUnknownHeiIdException(approvingHeiId);
    }

    IiasApprovalResponseV2 response = new IiasApprovalResponseV2();

    // NOTE: The algorithm handles each IIA ID individually as it may be necessary to fall back to
    // one or more providers.
    for (String iiaId : iiaIds) {
      List<InterInstitutionalAgreementsApprovalV2HostProvider> providersChain =
          getProvidersChainForHeiAndIiaId(approvingHeiId, iiaId);
      for (InterInstitutionalAgreementsApprovalV2HostProvider possibleProvider : providersChain) {
        Collection<Approval> providerResponse =
            possibleProvider.findByIiaIds(approvingHeiId, requesterCoveredHeiId, List.of(iiaId));
        if (!providerResponse.isEmpty()) {
          Approval approval = providerResponse.iterator().next();
          response.getApproval().add(approval);
          break;
        }
      }
    }

    return ResponseEntity.ok(response);
  }

  private List<InterInstitutionalAgreementsApprovalV2HostProvider> getProvidersChainForHeiAndIiaId(
      String heiId, String iiaId) throws EwpUnknownHeiIdException {

    if (!hostPluginManager.hasHostProvider(
        heiId, InterInstitutionalAgreementsApprovalV2HostProvider.class)) {
      return new ArrayList<>();
    }

    Optional<EwpInterInstitutionalAgreementMapping> mappingOptional =
        mappingRepository.findByHeiIdAndIiaId(heiId, iiaId);
    if (mappingOptional.isPresent()) {
      EwpInterInstitutionalAgreementMapping mapping = mappingOptional.get();
      Optional<InterInstitutionalAgreementsApprovalV2HostProvider> providerOptional =
          hostPluginManager.getSingleProvider(
              heiId,
              mapping.getOunitId(),
              InterInstitutionalAgreementsApprovalV2HostProvider.class);
      if (providerOptional.isPresent()) {
        InterInstitutionalAgreementsApprovalV2HostProvider provider = providerOptional.get();
        return List.of(provider);
      } else {
        throw new EwpUnknownOrganizationalUnitIdException(heiId, mapping.getOunitId());
      }

    } else {
      return hostPluginManager.getPrimaryFollowedByNonPrimaryProviders(
          heiId, InterInstitutionalAgreementsApprovalV2HostProvider.class);
    }
  }
}
