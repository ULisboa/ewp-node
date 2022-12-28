package pt.ulisboa.ewp.node.api.ewp.controller.iias.approvals;

import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.InterInstitutionalAgreementsApprovalV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
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
      @RequestParam(value = EwpApiParamConstants.APPROVING_HEI_ID, defaultValue = "") String approvingHeiId,
      @RequestParam(value = EwpApiParamConstants.OWNER_HEI_ID, defaultValue = "") String ownerHeiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID) Collection<String> iiaIds,
      @RequestParam(value = EwpApiParamConstants.SEND_PDF, required = false)
          Boolean sendPdf) {

    Map<InterInstitutionalAgreementsApprovalV1HostProvider, Collection<String>> providerToIiaIdsMap = getIiaIdsCoveredPerProviderOfHeiId(
        approvingHeiId, iiaIds);

    IiasApprovalResponseV1 response = new IiasApprovalResponseV1();
    for (Map.Entry<InterInstitutionalAgreementsApprovalV1HostProvider, Collection<String>> entry : providerToIiaIdsMap.entrySet()) {
      InterInstitutionalAgreementsApprovalV1HostProvider provider = entry.getKey();
      Collection<String> coveredIiaIds = entry.getValue();
      provider.findByIiaIds(approvingHeiId, ownerHeiId, coveredIiaIds, sendPdf)
          .forEach(iia -> response.getApproval().add(iia));
    }
    return ResponseEntity.ok(response);
  }

  private Map<InterInstitutionalAgreementsApprovalV1HostProvider, Collection<String>> getIiaIdsCoveredPerProviderOfHeiId(
      String heiId, Collection<String> iiaIds) throws EwpUnknownHeiIdException {

    if (!hostPluginManager.hasHostProvider(heiId,
        InterInstitutionalAgreementsApprovalV1HostProvider.class)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    Map<InterInstitutionalAgreementsApprovalV1HostProvider, Collection<String>> result = new HashMap<>();
    for (String iiaId : iiaIds) {
      Optional<EwpInterInstitutionalAgreementMapping> mappingOptional = mappingRepository.findByHeiIdAndIiaId(
          heiId, iiaId);
      if (mappingOptional.isPresent()) {
        EwpInterInstitutionalAgreementMapping mapping = mappingOptional.get();

        Optional<InterInstitutionalAgreementsApprovalV1HostProvider> providerOptional = hostPluginManager.getSingleProviderByHeiIdAndOunitId(
            heiId, mapping.getOunitId(),
            InterInstitutionalAgreementsApprovalV1HostProvider.class);
        if (providerOptional.isPresent()) {
          InterInstitutionalAgreementsApprovalV1HostProvider provider = providerOptional.get();
          result.computeIfAbsent(provider, ignored -> new ArrayList<>());
          result.get(provider).add(iiaId);
        }
      }
    }
    return result;
  }
}
