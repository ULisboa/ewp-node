package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.OmobilitiesGetResponseV2;
import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.OmobilitiesIndexResponseV2;
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
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV2HostProvider;
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
    EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilitiesV2Controller.BASE_PATH)
public class EwpApiOutgoingMobilitiesV2Controller {

  public static final String BASE_PATH = "omobilities/v2";

  private final HostPluginManager hostPluginManager;

  private final EwpOutgoingMobilityMappingRepository mappingRepository;

  public EwpApiOutgoingMobilitiesV2Controller(HostPluginManager hostPluginManager,
      EwpOutgoingMobilityMappingRepository mappingRepository) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
  }

  @RequestMapping(path = "/index", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Index API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesIndexResponseV2> outgoingMobilityIds(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID) String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID, required = false) Collection<String> receivingHeiIds,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, defaultValue = "") String receivingAcademicYearId,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
      @DateTimeFormat(iso = DATE_TIME) LocalDateTime modifiedSince,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId, OutgoingMobilitiesV2HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    Collection<OutgoingMobilitiesV2HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        sendingHeiId, OutgoingMobilitiesV2HostProvider.class);

    OmobilitiesIndexResponseV2 response = new OmobilitiesIndexResponseV2();
    providers.forEach(provider -> {
      Collection<String> outgoingMobilityIds = provider
          .findOutgoingMobilityIds(authenticationToken.getPrincipal().getHeiIdsCoveredByClient(),
              sendingHeiId, receivingHeiIds, receivingAcademicYearId,
              modifiedSince);
      response.getOmobilityId().addAll(outgoingMobilityIds);
    });
    return ResponseEntity.ok(response);
  }

  @RequestMapping(path = "/get", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Get API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesGetResponseV2> outgoingMobilities(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID, defaultValue = "") String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> outgoingMobilityIds,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId, OutgoingMobilitiesV2HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    outgoingMobilityIds =
        outgoingMobilityIds != null ? outgoingMobilityIds : Collections.emptyList();

    if (outgoingMobilityIds.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some Outgoing Mobility ID must be provided");
    }

    int maxOmobilityIdsPerRequest = hostPluginManager.getAllProvidersOfType(sendingHeiId,
            OutgoingMobilitiesV2HostProvider.class).stream().mapToInt(
            OutgoingMobilitiesV2HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);

    if (outgoingMobilityIds.size() > maxOmobilityIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid Outgoing Mobility IDs per request is "
              + maxOmobilityIdsPerRequest);
    }

    Map<OutgoingMobilitiesV2HostProvider, Collection<String>> providerToOmobilityIdsMap = getOmobilityIdsCoveredPerProviderOfHeiId(
        sendingHeiId, outgoingMobilityIds);

    OmobilitiesGetResponseV2 response = new OmobilitiesGetResponseV2();
    for (Map.Entry<OutgoingMobilitiesV2HostProvider, Collection<String>> entry : providerToOmobilityIdsMap.entrySet()) {
      OutgoingMobilitiesV2HostProvider provider = entry.getKey();
      Collection<String> coveredOmobilityIds = entry.getValue();
      provider.findBySendingHeiIdAndOutgoingMobilityIds(
              authenticationToken.getPrincipal().getHeiIdsCoveredByClient(), sendingHeiId,
              coveredOmobilityIds)
          .forEach(mobility -> response.getSingleMobilityObject().add(mobility));
    }
    return ResponseEntity.ok(response);
  }

  private Map<OutgoingMobilitiesV2HostProvider, Collection<String>> getOmobilityIdsCoveredPerProviderOfHeiId(
      String heiId, Collection<String> omobilityIds) {
    Map<OutgoingMobilitiesV2HostProvider, Collection<String>> result = new HashMap<>();
    for (String omobilityId : omobilityIds) {
      Optional<EwpOutgoingMobilityMapping> mappingOptional = mappingRepository.findByHeiIdAndOmobilityId(
          heiId, omobilityId);
      if (mappingOptional.isPresent()) {
        EwpOutgoingMobilityMapping mapping = mappingOptional.get();

        Collection<OutgoingMobilitiesV2HostProvider> providers = hostPluginManager.getProvidersByHeiIdAndOunitId(
            heiId, mapping.getOunitId(), OutgoingMobilitiesV2HostProvider.class);
        if (!providers.isEmpty()) {
          OutgoingMobilitiesV2HostProvider provider = providers.iterator().next();
          result.computeIfAbsent(provider, ignored -> new ArrayList<>());
          result.get(provider).add(omobilityId);
        }
      }
    }
    return result;
  }
}
