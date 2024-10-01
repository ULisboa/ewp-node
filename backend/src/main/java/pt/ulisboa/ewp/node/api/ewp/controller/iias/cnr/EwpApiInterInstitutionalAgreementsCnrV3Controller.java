package pt.ulisboa.ewp.node.api.ewp.controller.iias.cnr;

import eu.erasmuswithoutpaper.api.architecture.v1.EmptyV1;
import eu.erasmuswithoutpaper.api.iias.cnr.v3.IiaCnrResponseV3;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia.Partner;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.constraints.NotNull;
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
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.cnr.InterInstitutionalAgreementCnrV3HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.iias.EwpInterInstitutionalAgreementsV7Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.domain.entity.Host;
import pt.ulisboa.ewp.node.domain.repository.HostRepository;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsCnrV3Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementsCnrV3Controller {

  public static final String BASE_PATH = "iias/cnr/v3";

  private final HostPluginManager hostPluginManager;

  private final EwpInterInstitutionalAgreementsV7Client iiaClient;
  private final HostRepository hostRepository;

  public EwpApiInterInstitutionalAgreementsCnrV3Controller(
      HostPluginManager hostPluginManager,
      EwpInterInstitutionalAgreementsV7Client iiaClient,
      HostRepository hostRepository) {
    this.hostPluginManager = hostPluginManager;
    this.iiaClient = iiaClient;
    this.hostRepository = hostRepository;
  }

  @EwpApiEndpoint(api = "iia-cnr", apiMajorVersion = 3)
  @RequestMapping(
      path = "/{heiId}",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs CNR API.",
      tags = {"ewp"})
  public ResponseEntity<IiaCnrResponseV3> iiaCnr(
      EwpApiHostAuthenticationToken authenticationToken,
      @NotNull @PathVariable String heiId,
      @NotNull @RequestParam(value = EwpApiParamConstants.IIA_ID) String iiaId) {

    String requesterCoveredHeiId = authenticationToken.getPrincipal().getHeiIdsCoveredByClient().iterator().next();

    Iia iia = null;
    try {
      EwpSuccessOperationResult<IiasGetResponseV7> iiaGetResponse =
          iiaClient.findByHeiIdAndIiaIds(requesterCoveredHeiId, List.of(iiaId));
      if (!iiaGetResponse.getResponseBody().getIia().isEmpty()) {
        iia = iiaGetResponse.getResponseBody().getIia().iterator().next();
      }
    } catch (EwpClientErrorException e) {
      throw new RuntimeException(e);
    }

    if (iia == null) {
      propagateIiaDeletion(heiId, iiaId, requesterCoveredHeiId);
    } else {
      propagateIiaCreationOrModification(requesterCoveredHeiId, iia);
    }

    return ResponseEntity.ok(new IiaCnrResponseV3(new EmptyV1()));
  }

  private void propagateIiaCreationOrModification(String requesterCoveredHeiId, Iia iia)
      throws EwpBadRequestException {
    if (iia.getPartner().size() < 2) {
      throw new EwpBadRequestException("IIA must have at least two partners");
    }

    // NOTE: The specification specifies that the first partner is the HEI that is answering our
    // GET request (the notifier of the CNR call.
    // Hence, the second partner must be a local HEI covered by the node.
    Partner notifierPartner = iia.getPartner().get(0);
    if (!requesterCoveredHeiId.equals(notifierPartner.getHeiId())) {
      throw new EwpBadRequestException(
          "Expected first partner of IIA to be of HEI ID "
              + requesterCoveredHeiId
              + " but it is of "
              + notifierPartner.getHeiId()
              + " instead");
    }

    Partner localPartner = iia.getPartner().get(1);
    String localHeiId = localPartner.getHeiId();
    Optional<Host> hostOptional = hostRepository.findByCoveredHeiId(localHeiId);
    if (hostOptional.isEmpty()) {
      throw new EwpBadRequestException("HEI ID " + localHeiId + " is not covered by this node");
    }
    Host host = hostOptional.get();

    String localOunitId = localPartner.getOunitId();
    if (host.isOunitIdInObjectsRequired() && localOunitId == null) {
      throw new EwpBadRequestException(
          host.getOunitIdInObjectsRequiredErrorMessage(),
          host.getOunitIdInObjectsRequiredErrorMessage());
    }

    Optional<InterInstitutionalAgreementCnrV3HostProvider> providerOptional =
        hostPluginManager.getSingleProvider(
            localHeiId, localOunitId, InterInstitutionalAgreementCnrV3HostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpUnknownHeiIdException(localHeiId);
    }

    InterInstitutionalAgreementCnrV3HostProvider provider = providerOptional.get();
    provider.onChangeNotification(notifierPartner.getHeiId(), notifierPartner.getIiaId());
  }

  private void propagateIiaDeletion(String heiId, String iiaId, String requesterCoveredHeiId) {
    Collection<InterInstitutionalAgreementCnrV3HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(
            heiId, InterInstitutionalAgreementCnrV3HostProvider.class);
    for (InterInstitutionalAgreementCnrV3HostProvider provider : providers) {
      provider.onChangeNotification(requesterCoveredHeiId, iiaId);
    }
  }
}
