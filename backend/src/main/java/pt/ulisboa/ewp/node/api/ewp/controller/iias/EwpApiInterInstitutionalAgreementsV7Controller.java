package pt.ulisboa.ewp.node.api.ewp.controller.iias;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasIndexResponseV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasStatsResponseV7;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV7HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownOrganizationalUnitIdException;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashCalculationException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashCalculationResult;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.v7.IiaHashServiceV7;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementsV7Controller {

  public static final String BASE_PATH = "iias/v7";

  private final HostPluginManager hostPluginManager;

  private final EwpInterInstitutionalAgreementMappingRepository mappingRepository;
  private final IiaHashServiceV7 iiaHashService;

  private final String statsPortalHeiId;

  public EwpApiInterInstitutionalAgreementsV7Controller(
      HostPluginManager hostPluginManager,
      EwpInterInstitutionalAgreementMappingRepository mappingRepository,
      IiaHashServiceV7 iiaHashService,
      @Value("${stats.portal.heiId}") String statsPortalHeiId) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
    this.iiaHashService = iiaHashService;
    this.statsPortalHeiId = statsPortalHeiId;
  }

  private static IiasStatsResponseV7 createEmptyStatsResponse() {
    IiasStatsResponseV7 result = new IiasStatsResponseV7();
    result.setIiaFetchable(BigInteger.ZERO);
    result.setIiaLocalUnapprovedPartnerApproved(BigInteger.ZERO);
    result.setIiaLocalApprovedPartnerUnapproved(BigInteger.ZERO);
    result.setIiaBothApproved(BigInteger.ZERO);
    return result;
  }

  private static IiasStatsResponseV7 mergeStatsResponses(
      IiasStatsResponseV7 first, IiasStatsResponseV7 second) {

    if (second == null) {
      return first;
    }

    IiasStatsResponseV7 result = new IiasStatsResponseV7();

    result.setIiaFetchable(BigInteger.ZERO);
    if (first.getIiaFetchable() != null) {
      result.setIiaFetchable(result.getIiaFetchable().add(first.getIiaFetchable()));
    }
    if (second.getIiaFetchable() != null) {
      result.setIiaFetchable(result.getIiaFetchable().add(second.getIiaFetchable()));
    }

    result.setIiaLocalUnapprovedPartnerApproved(BigInteger.ZERO);
    if (first.getIiaLocalUnapprovedPartnerApproved() != null) {
      result.setIiaLocalUnapprovedPartnerApproved(
          result
              .getIiaLocalUnapprovedPartnerApproved()
              .add(first.getIiaLocalUnapprovedPartnerApproved()));
    }
    if (second.getIiaLocalUnapprovedPartnerApproved() != null) {
      result.setIiaLocalUnapprovedPartnerApproved(
          result
              .getIiaLocalUnapprovedPartnerApproved()
              .add(second.getIiaLocalUnapprovedPartnerApproved()));
    }

    result.setIiaLocalApprovedPartnerUnapproved(BigInteger.ZERO);
    if (first.getIiaLocalApprovedPartnerUnapproved() != null) {
      result.setIiaLocalApprovedPartnerUnapproved(
          result
              .getIiaLocalApprovedPartnerUnapproved()
              .add(first.getIiaLocalApprovedPartnerUnapproved()));
    }
    if (second.getIiaLocalApprovedPartnerUnapproved() != null) {
      result.setIiaLocalApprovedPartnerUnapproved(
          result
              .getIiaLocalApprovedPartnerUnapproved()
              .add(second.getIiaLocalApprovedPartnerUnapproved()));
    }

    result.setIiaBothApproved(BigInteger.ZERO);
    if (first.getIiaBothApproved() != null) {
      result.setIiaBothApproved(result.getIiaBothApproved().add(first.getIiaBothApproved()));
    }
    if (second.getIiaBothApproved() != null) {
      result.setIiaBothApproved(result.getIiaBothApproved().add(second.getIiaBothApproved()));
    }

    return result;
  }

  @RequestMapping(
      path = "/{heiId}/index",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Index API.",
      tags = {"ewp"})
  public ResponseEntity<IiasIndexResponseV7> iiaIds(
      EwpApiHostAuthenticationToken authenticationToken,
      @PathVariable String heiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, required = false)
          Collection<String> receivingAcademicYearIds,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
          @DateTimeFormat(iso = DATE_TIME)
          LocalDateTime modifiedSince) {

    if (!hostPluginManager.hasHostProvider(
        heiId, InterInstitutionalAgreementsV7HostProvider.class)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    Collection<InterInstitutionalAgreementsV7HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(
            heiId, InterInstitutionalAgreementsV7HostProvider.class);

    IiasIndexResponseV7 response = new IiasIndexResponseV7();
    providers.forEach(
        provider -> {
          Collection<String> iiaIds =
              provider.findAllIiaIdsByHeiId(
                  authenticationToken.getPrincipal().getHeiIdsCoveredByClient().iterator().next(),
                  heiId,
                  receivingAcademicYearIds,
                  modifiedSince);
          response.getIiaId().addAll(iiaIds);
        });
    return ResponseEntity.ok(response);
  }

  @RequestMapping(
      path = "/{heiId}/get",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Get API.",
      tags = {"ewp"})
  public ResponseEntity<IiasGetResponseV7> iiasGet(
      EwpApiHostAuthenticationToken authenticationToken,
      @PathVariable String heiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID, required = false) List<String> iiaIds)
      throws HashCalculationException {

    iiaIds = iiaIds != null ? iiaIds : Collections.emptyList();

    if (!hostPluginManager.hasHostProvider(
        heiId, InterInstitutionalAgreementsV7HostProvider.class)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    if (iiaIds.isEmpty()) {
      throw new EwpBadRequestException("At least some IIA ID must be provided");
    }

    int maxIiaIdsPerRequest =
        hostPluginManager
            .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV7HostProvider.class)
            .stream()
            .mapToInt(InterInstitutionalAgreementsV7HostProvider::getMaxIiaIdsPerRequest)
            .min()
            .orElse(0);

    if (iiaIds.size() > maxIiaIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid IIA IDs per request is " + maxIiaIdsPerRequest);
    }

    String requesterCoveredHeiId = authenticationToken.getPrincipal().getHeiIdsCoveredByClient().iterator().next();

    IiasGetResponseV7 response = new IiasGetResponseV7();

    // NOTE: The algorithm handles each IIA ID individually as it may be necessary to fall back to
    // one or more providers.
    for (String iiaId : iiaIds) {
      List<InterInstitutionalAgreementsV7HostProvider> providersChain = getProvidersChainForHeiAndIiaId(
          heiId, iiaId);
      for (InterInstitutionalAgreementsV7HostProvider possibleProvider : providersChain) {
        Collection<IiasGetResponseV7.Iia> providerResponse =
            possibleProvider.findByHeiIdAndIiaIds(requesterCoveredHeiId, heiId, List.of(iiaId));
        if (!providerResponse.isEmpty()) {
          Iia iia = providerResponse.iterator().next();
          if (StringUtils.isEmpty(iia.getIiaHash())) {
            List<HashCalculationResult> hashCalculationResults =
                this.iiaHashService.calculateIiaHashes(List.of(iia));
            iia.setIiaHash(hashCalculationResults.get(0).getHash());
          }
          response.getIia().add(iia);
          break;
        }
      }
    }

    return ResponseEntity.ok(response);
  }

  @RequestMapping(
      path = "/{heiId}/stats",
      method = {RequestMethod.GET},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Stats API.",
      tags = {"ewp"})
  public ResponseEntity<IiasStatsResponseV7> getStats(
      @PathVariable String heiId, EwpApiHostAuthenticationToken authenticationToken) {

    if (!authenticationToken.getPrincipal().getHeiIdsCoveredByClient().contains(statsPortalHeiId)) {
      throw new EwpBadRequestException(
          "Unauthorized HEI IDs: " + authenticationToken.getPrincipal().getHeiIdsCoveredByClient());
    }

    Collection<InterInstitutionalAgreementsV7HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(
            heiId, InterInstitutionalAgreementsV7HostProvider.class);

    IiasStatsResponseV7 statsResponse = createEmptyStatsResponse();
    for (InterInstitutionalAgreementsV7HostProvider provider : providers) {
      IiasStatsResponseV7 newStats = provider.getStats(heiId);
      statsResponse = mergeStatsResponses(statsResponse, newStats);
    }

    return ResponseEntity.ok(statsResponse);
  }

  private List<InterInstitutionalAgreementsV7HostProvider> getProvidersChainForHeiAndIiaId(
      String heiId, String iiaId) throws EwpUnknownHeiIdException {

    if (!hostPluginManager.hasHostProvider(
        heiId, InterInstitutionalAgreementsV7HostProvider.class)) {
      return new ArrayList<>();
    }

    Optional<EwpInterInstitutionalAgreementMapping> mappingOptional =
        mappingRepository.findByHeiIdAndIiaId(heiId, iiaId);
    if (mappingOptional.isPresent()) {
      EwpInterInstitutionalAgreementMapping mapping = mappingOptional.get();
      Optional<InterInstitutionalAgreementsV7HostProvider> providerOptional =
          hostPluginManager.getSingleProvider(
              heiId, mapping.getOunitId(), InterInstitutionalAgreementsV7HostProvider.class);
      if (providerOptional.isPresent()) {
        InterInstitutionalAgreementsV7HostProvider provider = providerOptional.get();
        return List.of(provider);
      } else {
        throw new EwpUnknownOrganizationalUnitIdException(heiId, mapping.getOunitId());
      }

    } else {
      return hostPluginManager.getPrimaryFollowedByNonPrimaryProviders(
          heiId, InterInstitutionalAgreementsV7HostProvider.class);
    }
  }

}
