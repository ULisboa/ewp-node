package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.las;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasIndexResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateRequestV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import javax.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.exceptions.EditConflictException;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.OutgoingMobilityLearningAgreementsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpOutgoingMobilityMappingRepository;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH)
public class EwpApiOutgoingMobilityLearningAgreementsV1Controller {

  public static final String BASE_PATH = "omobilities/las/v1";

  private final HostPluginManager hostPluginManager;

  private final EwpOutgoingMobilityMappingRepository mappingRepository;

  public EwpApiOutgoingMobilityLearningAgreementsV1Controller(HostPluginManager hostPluginManager,
      EwpOutgoingMobilityMappingRepository mappingRepository) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
  }

  @RequestMapping(path = "/index", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobility Learning Agreements Index API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilityLasIndexResponseV1> outgoingMobilityIds(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID) String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID, required = false) Collection<String> receivingHeiIds,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, defaultValue = "") String receivingAcademicYearId,
      @RequestParam(value = EwpApiParamConstants.GLOBAL_ID, defaultValue = "") String globalId,
      @RequestParam(value = EwpApiParamConstants.MOBILITY_TYPE, defaultValue = "") String mobilityType,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
      @DateTimeFormat(iso = DATE_TIME) LocalDateTime modifiedSince,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId,
        OutgoingMobilityLearningAgreementsV1HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    Collection<OutgoingMobilityLearningAgreementsV1HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        sendingHeiId, OutgoingMobilityLearningAgreementsV1HostProvider.class);

    OmobilityLasIndexResponseV1 response = new OmobilityLasIndexResponseV1();
    providers.forEach(provider -> {
      Collection<String> outgoingMobilityIds = provider
          .findOutgoingMobilityIds(authenticationToken.getPrincipal().getHeiIdsCoveredByClient(),
              sendingHeiId, receivingHeiIds, receivingAcademicYearId, globalId, mobilityType,
              modifiedSince);
      response.getOmobilityId().addAll(outgoingMobilityIds);
    });
    return ResponseEntity.ok(response);
  }

  @RequestMapping(path = "/get", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobility Learning Agreements Get API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilityLasGetResponseV1> learningAgreements(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID, defaultValue = "") String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> outgoingMobilityIds,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId,
        OutgoingMobilityLearningAgreementsV1HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    outgoingMobilityIds =
        outgoingMobilityIds != null ? outgoingMobilityIds : Collections.emptyList();

    if (outgoingMobilityIds.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some Outgoing Mobility ID must be provided");
    }

    int maxOmobilityIdsPerRequest = hostPluginManager.getAllProvidersOfType(sendingHeiId,
            OutgoingMobilityLearningAgreementsV1HostProvider.class).stream().mapToInt(
            OutgoingMobilityLearningAgreementsV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);

    if (outgoingMobilityIds.size() > maxOmobilityIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid Outgoing Mobility IDs per request is "
              + maxOmobilityIdsPerRequest);
    }

    Map<OutgoingMobilityLearningAgreementsV1HostProvider, Collection<String>> providerToOmobilityIdsMap = getOmobilityIdsCoveredPerProviderOfHeiId(
        sendingHeiId, outgoingMobilityIds);

    OmobilityLasGetResponseV1 response = new OmobilityLasGetResponseV1();
    for (Map.Entry<OutgoingMobilityLearningAgreementsV1HostProvider, Collection<String>> entry : providerToOmobilityIdsMap.entrySet()) {
      OutgoingMobilityLearningAgreementsV1HostProvider provider = entry.getKey();
      Collection<String> coveredOmobilityIds = entry.getValue();
      provider.findBySendingHeiIdAndOutgoingMobilityIds(
              authenticationToken.getPrincipal().getHeiIdsCoveredByClient(), sendingHeiId,
              coveredOmobilityIds)
          .forEach(la -> response.getLa().add(la));
    }
    return ResponseEntity.ok(response);
  }

  @RequestMapping(path = "/update", method = {
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobility Learning Agreements Update API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilityLasUpdateResponseV1> learningAgreementUpdate(
      @Valid @RequestBody OmobilityLasUpdateRequestV1 updateData,
      EwpApiHostAuthenticationToken authenticationToken) throws EditConflictException {
    String sendingHeiId = updateData.getSendingHeiId();

    String omobilityId = getOmobilityIdOfUpdateData(updateData);

    Optional<EwpOutgoingMobilityMapping> mappingOptional = mappingRepository.findByHeiIdAndOmobilityId(
        sendingHeiId, omobilityId);
    if (mappingOptional.isEmpty()) {
      throw new EwpBadRequestException(
          "Unknown learning agreement with omobility_id: " + omobilityId);
    }

    Optional<OutgoingMobilityLearningAgreementsV1HostProvider> providerOptional = hostPluginManager.getProvider(
        sendingHeiId, mappingOptional.get().getOunitId(),
        OutgoingMobilityLearningAgreementsV1HostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }
    OutgoingMobilityLearningAgreementsV1HostProvider provider = providerOptional.get();

    OmobilityLasUpdateResponseV1 response = provider.updateOutgoingMobilityLearningAgreement(
        authenticationToken.getPrincipal().getHeiIdsCoveredByClient(), updateData);
    return ResponseEntity.ok(response);
  }

  private String getOmobilityIdOfUpdateData(OmobilityLasUpdateRequestV1 updateData) {
    if (updateData.getApproveProposalV1() != null) {
      return updateData.getApproveProposalV1().getOmobilityId();
    } else {
      return updateData.getCommentProposalV1().getOmobilityId();
    }
  }

  private Map<OutgoingMobilityLearningAgreementsV1HostProvider, Collection<String>> getOmobilityIdsCoveredPerProviderOfHeiId(
      String heiId, Collection<String> omobilityIds) {
    Map<OutgoingMobilityLearningAgreementsV1HostProvider, Collection<String>> result = new HashMap<>();
    for (String omobilityId : omobilityIds) {
      Optional<EwpOutgoingMobilityMapping> mappingOptional = mappingRepository.findByHeiIdAndOmobilityId(
          heiId, omobilityId);
      if (mappingOptional.isPresent()) {
        EwpOutgoingMobilityMapping mapping = mappingOptional.get();

        Collection<OutgoingMobilityLearningAgreementsV1HostProvider> providers = hostPluginManager.getProvidersByHeiIdAndOunitId(
            heiId, mapping.getOunitId(), OutgoingMobilityLearningAgreementsV1HostProvider.class);
        if (!providers.isEmpty()) {
          OutgoingMobilityLearningAgreementsV1HostProvider provider = providers.iterator().next();
          result.computeIfAbsent(provider, ignored -> new ArrayList<>());
          result.get(provider).add(omobilityId);
        }
      }
    }
    return result;
  }
}
