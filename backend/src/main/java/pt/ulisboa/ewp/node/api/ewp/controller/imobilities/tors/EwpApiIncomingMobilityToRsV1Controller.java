package pt.ulisboa.ewp.node.api.ewp.controller.imobilities.tors;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.imobilities.tors.v1.endpoints.ImobilityTorsGetResponseV1;
import eu.erasmuswithoutpaper.api.imobilities.tors.v1.endpoints.ImobilityTorsIndexResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.tors.IncomingMobilityToRsV1HostProvider;
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
    EwpApiConstants.API_BASE_URI + EwpApiIncomingMobilityToRsV1Controller.BASE_PATH)
public class EwpApiIncomingMobilityToRsV1Controller {

  public static final String BASE_PATH = "imobilities/tors/v1";

  private final HostPluginManager hostPluginManager;

  private final EwpOutgoingMobilityMappingRepository mappingRepository;

  public EwpApiIncomingMobilityToRsV1Controller(HostPluginManager hostPluginManager,
      EwpOutgoingMobilityMappingRepository mappingRepository) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
  }

  @RequestMapping(path = "/index", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Incoming Mobility ToRs Index API.",
      tags = {"ewp"})
  public ResponseEntity<ImobilityTorsIndexResponseV1> outgoingMobilityIdsWithTranscriptsOfRecordsAttached(
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID) String receivingHeiId,
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID, required = false) Collection<String> sendingHeiIds,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
      @DateTimeFormat(iso = DATE_TIME) LocalDateTime modifiedSince,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(receivingHeiId,
        IncomingMobilityToRsV1HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + receivingHeiId);
    }

    Collection<IncomingMobilityToRsV1HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        receivingHeiId, IncomingMobilityToRsV1HostProvider.class);

    ImobilityTorsIndexResponseV1 response = new ImobilityTorsIndexResponseV1();
    providers.forEach(provider -> {
      Collection<String> outgoingMobilityIds = provider
          .findOutgoingMobilityIds(authenticationToken.getPrincipal().getHeiIdsCoveredByClient(),
              receivingHeiId, sendingHeiIds, modifiedSince);
      response.getOmobilityId().addAll(outgoingMobilityIds);
    });
    return ResponseEntity.ok(response);
  }

  @RequestMapping(path = "/get", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Incoming Mobility ToRs Get API.",
      tags = {"ewp"})
  public ResponseEntity<ImobilityTorsGetResponseV1> transcriptOfRecords(
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID) String receivingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> outgoingMobilityIds,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(receivingHeiId,
        IncomingMobilityToRsV1HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + receivingHeiId);
    }

    outgoingMobilityIds =
        outgoingMobilityIds != null ? outgoingMobilityIds : Collections.emptyList();

    if (outgoingMobilityIds.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some Outgoing Mobility ID must be provided");
    }

    int maxOmobilityIdsPerRequest = hostPluginManager.getAllProvidersOfType(receivingHeiId,
            IncomingMobilityToRsV1HostProvider.class).stream().mapToInt(
            IncomingMobilityToRsV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);

    if (outgoingMobilityIds.size() > maxOmobilityIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid Outgoing Mobility IDs per request is "
              + maxOmobilityIdsPerRequest);
    }

    Map<IncomingMobilityToRsV1HostProvider, Collection<String>> providerToOmobilityIdsMap = getOmobilityIdsCoveredPerProviderOfHeiId(
        receivingHeiId, outgoingMobilityIds);

    ImobilityTorsGetResponseV1 response = new ImobilityTorsGetResponseV1();
    for (Map.Entry<IncomingMobilityToRsV1HostProvider, Collection<String>> entry : providerToOmobilityIdsMap.entrySet()) {
      IncomingMobilityToRsV1HostProvider provider = entry.getKey();
      Collection<String> coveredOmobilityIds = entry.getValue();
      provider.findByReceivingHeiIdAndOutgoingMobilityIds(
              authenticationToken.getPrincipal().getHeiIdsCoveredByClient(), receivingHeiId,
              coveredOmobilityIds)
          .forEach(tor -> response.getTor().add(tor));
    }
    return ResponseEntity.ok(response);
  }

  private Map<IncomingMobilityToRsV1HostProvider, Collection<String>> getOmobilityIdsCoveredPerProviderOfHeiId(
      String heiId, Collection<String> omobilityIds) {
    Map<IncomingMobilityToRsV1HostProvider, Collection<String>> result = new HashMap<>();
    for (String omobilityId : omobilityIds) {
      Optional<EwpOutgoingMobilityMapping> mappingOptional = mappingRepository.findByHeiIdAndOmobilityId(
          heiId, omobilityId);
      if (mappingOptional.isPresent()) {
        EwpOutgoingMobilityMapping mapping = mappingOptional.get();

        Optional<IncomingMobilityToRsV1HostProvider> providerOptional = hostPluginManager.getSingleProvider(
            heiId, mapping.getOunitId(), IncomingMobilityToRsV1HostProvider.class);
        if (providerOptional.isPresent()) {
          IncomingMobilityToRsV1HostProvider provider = providerOptional.get();
          result.computeIfAbsent(provider, ignored -> new ArrayList<>());
          result.get(provider).add(omobilityId);
        }
      }
    }
    return result;
  }
}
