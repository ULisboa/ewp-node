package pt.ulisboa.ewp.node.api.ewp.controller.iias;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasIndexResponseV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasStatsResponseV6;
import io.swagger.v3.oas.annotations.Operation;
import java.math.BigInteger;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV6HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
import pt.ulisboa.ewp.node.exception.ewp.hash.HashCalculationException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.HashCalculationResult;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.IiaHashService;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH)
public class EwpApiInterInstitutionalAgreementsV6Controller {

  public static final String BASE_PATH = "iias/v6";

  private final HostPluginManager hostPluginManager;

  private final EwpInterInstitutionalAgreementMappingRepository mappingRepository;
  private final IiaHashService iiaHashService;

  private final String statsPortalHeiId;

  public EwpApiInterInstitutionalAgreementsV6Controller(HostPluginManager hostPluginManager,
      EwpInterInstitutionalAgreementMappingRepository mappingRepository,
      IiaHashService iiaHashService, @Value("${stats.portal.heiId}") String statsPortalHeiId) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
    this.iiaHashService = iiaHashService;
    this.statsPortalHeiId = statsPortalHeiId;
  }

  @RequestMapping(path = "/index", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Index API.",
      tags = {"ewp"})
  public ResponseEntity<IiasIndexResponseV6> iiaIds(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.PARTNER_HEI_ID, defaultValue = "") String partnerHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, required = false) Collection<String> receivingAcademicYearIds,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
      @DateTimeFormat(iso = DATE_TIME) LocalDateTime modifiedSince) {

    if (!hostPluginManager.hasHostProvider(heiId,
        InterInstitutionalAgreementsV6HostProvider.class)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    Collection<InterInstitutionalAgreementsV6HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        heiId, InterInstitutionalAgreementsV6HostProvider.class);

    IiasIndexResponseV6 response = new IiasIndexResponseV6();
    providers.forEach(provider -> {
      Collection<String> iiaIds = provider.findAllIiaIdsByHeiId(Collections.singletonList(heiId),
          heiId, partnerHeiId,
          receivingAcademicYearIds, modifiedSince);
      response.getIiaId().addAll(iiaIds);
    });
    return ResponseEntity.ok(response);
  }

  @RequestMapping(path = "/get", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "IIAs Get API.",
      tags = {"ewp"})
  public ResponseEntity<IiasGetResponseV6> iiasGet(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.IIA_ID, required = false)
      List<String> iiaIds,
      @RequestParam(value = EwpApiParamConstants.IIA_CODE, required = false)
      List<String> iiaCodes,
      @RequestParam(value = EwpApiParamConstants.SEND_PDF, required = false)
      Boolean sendPdf) throws HashCalculationException {

    iiaIds = iiaIds != null ? iiaIds : Collections.emptyList();
    iiaCodes = iiaCodes != null ? iiaCodes : Collections.emptyList();

    if (!hostPluginManager.hasHostProvider(heiId,
        InterInstitutionalAgreementsV6HostProvider.class)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    if (!iiaIds.isEmpty() && !iiaCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "Only IIA IDs or codes are accepted, not both simultaneously");
    }

    if (iiaIds.isEmpty() && iiaCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some IIA ID or code must be provided");
    }

    if (!iiaIds.isEmpty()) {
      return iiasByIds(heiId, iiaIds, sendPdf);
    } else {
      return iiasByCodes(heiId, iiaCodes, sendPdf);
    }
  }

  @RequestMapping(path = "/stats", method = {
      RequestMethod.GET}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(summary = "IIAs Stats API.", tags = {"ewp"})
  public ResponseEntity<IiasStatsResponseV6> getStats(
      @RequestParam(EwpApiParamConstants.HEI_ID) String heiId,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!authenticationToken.getPrincipal().getHeiIdsCoveredByClient().contains(statsPortalHeiId)) {
      throw new EwpBadRequestException(
          "Unauthorized HEI IDs: " + authenticationToken.getPrincipal().getHeiIdsCoveredByClient());
    }

    Collection<InterInstitutionalAgreementsV6HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(heiId,
            InterInstitutionalAgreementsV6HostProvider.class);

    IiasStatsResponseV6 statsResponse = createEmptyStatsResponse();
    for (InterInstitutionalAgreementsV6HostProvider provider : providers) {
      IiasStatsResponseV6 newStats = provider.getStats(heiId);
      statsResponse = mergeStatsResponses(statsResponse, newStats);
    }

    return ResponseEntity.ok(statsResponse);
  }

  private ResponseEntity<IiasGetResponseV6> iiasByIds(String heiId, List<String> iiaIds,
      Boolean sendPdf) throws HashCalculationException {

    Map<InterInstitutionalAgreementsV6HostProvider, Collection<String>> providerToIiaIdsMap = getIiaIdsCoveredPerProviderOfHeiId(
        heiId, iiaIds);

    int maxIiaIdsPerRequest = hostPluginManager.getAllProvidersOfType(heiId,
            InterInstitutionalAgreementsV6HostProvider.class).stream().mapToInt(
            InterInstitutionalAgreementsV6HostProvider::getMaxIiaIdsPerRequest)
        .min().orElse(0);

    if (iiaIds.size() > maxIiaIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid IIA IDs per request is "
              + maxIiaIdsPerRequest);
    }

    IiasGetResponseV6 response = new IiasGetResponseV6();
    for (Map.Entry<InterInstitutionalAgreementsV6HostProvider, Collection<String>> entry : providerToIiaIdsMap.entrySet()) {
      InterInstitutionalAgreementsV6HostProvider provider = entry.getKey();
      Collection<String> coveredIiaIds = entry.getValue();
      Collection<Iia> iias = provider.findByHeiIdAndIiaIds(Collections.singletonList(heiId), heiId,
          coveredIiaIds, sendPdf);
      for (Iia iia : iias) {
        List<HashCalculationResult> hashCalculationResults = this.iiaHashService.calculateCooperationConditionsHashesForV6(
            List.of(iia));
        iia.setConditionsHash(hashCalculationResults.get(0).getHash());
        response.getIia().add(iia);
      }
    }
    return ResponseEntity.ok(response);
  }

  private ResponseEntity<IiasGetResponseV6> iiasByCodes(String heiId, List<String> iiaCodes,
      Boolean sendPdf) throws HashCalculationException {

    Map<InterInstitutionalAgreementsV6HostProvider, Collection<String>> providerToIiaCodesMap = getIiaCodesCoveredPerProviderOfHeiId(
        heiId, iiaCodes);

    int maxIiaCodesPerRequest = hostPluginManager.getAllProvidersOfType(heiId,
            InterInstitutionalAgreementsV6HostProvider.class).stream().mapToInt(
            InterInstitutionalAgreementsV6HostProvider::getMaxIiaCodesPerRequest)
        .min().orElse(0);

    if (iiaCodes.size() > maxIiaCodesPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid IIA codes per request is "
              + maxIiaCodesPerRequest);
    }

    IiasGetResponseV6 response = new IiasGetResponseV6();
    for (Map.Entry<InterInstitutionalAgreementsV6HostProvider, Collection<String>> entry : providerToIiaCodesMap.entrySet()) {
      InterInstitutionalAgreementsV6HostProvider provider = entry.getKey();
      Collection<String> coveredIiaCodes = entry.getValue();
      Collection<Iia> iias = provider.findByHeiIdAndIiaCodes(Collections.singletonList(heiId),
          heiId, coveredIiaCodes, sendPdf);
      for (Iia iia : iias) {
        List<HashCalculationResult> hashCalculationResults = this.iiaHashService.calculateCooperationConditionsHashesForV6(
            List.of(iia));
        iia.setConditionsHash(hashCalculationResults.get(0).getHash());
        response.getIia().add(iia);
      }
    }
    return ResponseEntity.ok(response);
  }

  private Map<InterInstitutionalAgreementsV6HostProvider, Collection<String>> getIiaIdsCoveredPerProviderOfHeiId(
      String heiId, Collection<String> iiaIds) throws EwpUnknownHeiIdException {

    if (!hostPluginManager.hasHostProvider(heiId,
        InterInstitutionalAgreementsV6HostProvider.class)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    Map<InterInstitutionalAgreementsV6HostProvider, Collection<String>> result = new HashMap<>();
    for (String iiaId : iiaIds) {
      Optional<EwpInterInstitutionalAgreementMapping> mappingOptional = mappingRepository.findByHeiIdAndIiaId(
          heiId, iiaId);
      if (mappingOptional.isPresent()) {
        EwpInterInstitutionalAgreementMapping mapping = mappingOptional.get();

        Optional<InterInstitutionalAgreementsV6HostProvider> providerOptional = hostPluginManager.getSingleProvider(
            heiId, mapping.getOunitId(),
            InterInstitutionalAgreementsV6HostProvider.class);
        if (providerOptional.isPresent()) {
          InterInstitutionalAgreementsV6HostProvider provider = providerOptional.get();
          result.computeIfAbsent(provider, ignored -> new ArrayList<>());
          result.get(provider).add(iiaId);
        }
      }
    }
    return result;
  }

  private Map<InterInstitutionalAgreementsV6HostProvider, Collection<String>> getIiaCodesCoveredPerProviderOfHeiId(
      String heiId, Collection<String> iiaCodes) throws EwpUnknownHeiIdException {

    if (!hostPluginManager.hasHostProvider(heiId,
        InterInstitutionalAgreementsV6HostProvider.class)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    Map<InterInstitutionalAgreementsV6HostProvider, Collection<String>> result = new HashMap<>();
    for (String iiaCode : iiaCodes) {
      Optional<EwpInterInstitutionalAgreementMapping> mappingOptional = mappingRepository.findByHeiIdAndIiaCode(
          heiId, iiaCode);
      if (mappingOptional.isPresent()) {
        EwpInterInstitutionalAgreementMapping mapping = mappingOptional.get();

        Optional<InterInstitutionalAgreementsV6HostProvider> providerOptional = hostPluginManager.getSingleProvider(
            heiId, mapping.getOunitId(),
            InterInstitutionalAgreementsV6HostProvider.class);
        if (providerOptional.isPresent()) {
          InterInstitutionalAgreementsV6HostProvider provider = providerOptional.get();
          result.computeIfAbsent(provider, ignored -> new ArrayList<>());
          result.get(provider).add(iiaCode);
        }
      }
    }
    return result;
  }

  private static IiasStatsResponseV6 createEmptyStatsResponse() {
    IiasStatsResponseV6 result = new IiasStatsResponseV6();
    result.setIiaFetchable(BigInteger.ZERO);
    result.setIiaLocalUnapprovedPartnerApproved(BigInteger.ZERO);
    result.setIiaLocalApprovedPartnerUnapproved(BigInteger.ZERO);
    result.setIiaBothApproved(BigInteger.ZERO);
    return result;
  }

  private static IiasStatsResponseV6 mergeStatsResponses(IiasStatsResponseV6 first,
      IiasStatsResponseV6 second) {

    if (second == null) {
      return first;
    }

    IiasStatsResponseV6 result = new IiasStatsResponseV6();

    result.setIiaFetchable(BigInteger.ZERO);
    if (first.getIiaFetchable() != null) {
      result.setIiaFetchable(
          result.getIiaFetchable()
              .add(first.getIiaFetchable()));
    }
    if (second.getIiaFetchable() != null) {
      result.setIiaFetchable(
          result.getIiaFetchable()
              .add(second.getIiaFetchable()));
    }

    result.setIiaLocalUnapprovedPartnerApproved(BigInteger.ZERO);
    if (first.getIiaLocalUnapprovedPartnerApproved() != null) {
      result.setIiaLocalUnapprovedPartnerApproved(
          result.getIiaLocalUnapprovedPartnerApproved()
              .add(first.getIiaLocalUnapprovedPartnerApproved()));
    }
    if (second.getIiaLocalUnapprovedPartnerApproved() != null) {
      result.setIiaLocalUnapprovedPartnerApproved(
          result.getIiaLocalUnapprovedPartnerApproved()
              .add(second.getIiaLocalUnapprovedPartnerApproved()));
    }

    result.setIiaLocalApprovedPartnerUnapproved(BigInteger.ZERO);
    if (first.getIiaLocalApprovedPartnerUnapproved() != null) {
      result.setIiaLocalApprovedPartnerUnapproved(
          result.getIiaLocalApprovedPartnerUnapproved()
              .add(first.getIiaLocalApprovedPartnerUnapproved()));
    }
    if (second.getIiaLocalApprovedPartnerUnapproved() != null) {
      result.setIiaLocalApprovedPartnerUnapproved(
          result.getIiaLocalApprovedPartnerUnapproved()
              .add(second.getIiaLocalApprovedPartnerUnapproved()));
    }

    result.setIiaBothApproved(BigInteger.ZERO);
    if (first.getIiaBothApproved() != null) {
      result.setIiaBothApproved(
          result.getIiaBothApproved()
              .add(first.getIiaBothApproved()));
    }
    if (second.getIiaBothApproved() != null) {
      result.setIiaBothApproved(
          result.getIiaBothApproved()
              .add(second.getIiaBothApproved()));
    }

    return result;
  }
}