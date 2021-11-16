package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesIndexResponseV1;
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
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV1HostProvider;
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
    EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH)
public class EwpApiOutgoingMobilitiesV1Controller {

  public static final String BASE_PATH = "omobilities/v1";

  private final HostPluginManager hostPluginManager;

  private final EwpOutgoingMobilityMappingRepository mappingRepository;

  public EwpApiOutgoingMobilitiesV1Controller(HostPluginManager hostPluginManager,
      EwpOutgoingMobilityMappingRepository mappingRepository) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
  }

  @RequestMapping(path = "/index", method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Index API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesIndexResponseV1> outgoingMobilityIds(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID) String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID, required = false) Collection<String> receivingHeiIds,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, defaultValue = "") String receivingAcademicYearId,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
      @DateTimeFormat(iso = DATE_TIME) LocalDateTime modifiedSince,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId, OutgoingMobilitiesV1HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    Collection<OutgoingMobilitiesV1HostProvider> providers = hostPluginManager.getAllProvidersOfType(
        sendingHeiId, OutgoingMobilitiesV1HostProvider.class);

    OmobilitiesIndexResponseV1 response = new OmobilitiesIndexResponseV1();
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
  public ResponseEntity<OmobilitiesGetResponseV1> outgoingMobilities(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID, defaultValue = "") String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> outgoingMobilityIds,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId, OutgoingMobilitiesV1HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    outgoingMobilityIds =
        outgoingMobilityIds != null ? outgoingMobilityIds : Collections.emptyList();

    if (outgoingMobilityIds.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some Outgoing Mobility ID must be provided");
    }

    int maxOmobilityIdsPerRequest = hostPluginManager.getAllProvidersOfType(sendingHeiId,
            OutgoingMobilitiesV1HostProvider.class).stream().mapToInt(
            OutgoingMobilitiesV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);

    if (outgoingMobilityIds.size() > maxOmobilityIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid Outgoing Mobility IDs per request is "
              + maxOmobilityIdsPerRequest);
    }

    Map<OutgoingMobilitiesV1HostProvider, Collection<String>> providerToOmobilityIdsMap = getOmobilityIdsCoveredPerProviderOfHeiId(
        sendingHeiId, outgoingMobilityIds);

    OmobilitiesGetResponseV1 response = new OmobilitiesGetResponseV1();
    for (Map.Entry<OutgoingMobilitiesV1HostProvider, Collection<String>> entry : providerToOmobilityIdsMap.entrySet()) {
      OutgoingMobilitiesV1HostProvider provider = entry.getKey();
      Collection<String> coveredOmobilityIds = entry.getValue();
      provider.findBySendingHeiIdAndOutgoingMobilityIds(
              authenticationToken.getPrincipal().getHeiIdsCoveredByClient(), sendingHeiId,
              coveredOmobilityIds)
          .forEach(mobility -> response.getSingleMobilityObject().add(mobility));
    }
    return ResponseEntity.ok(response);
  }

  private Map<OutgoingMobilitiesV1HostProvider, Collection<String>> getOmobilityIdsCoveredPerProviderOfHeiId(
      String heiId, Collection<String> omobilityIds) {
    Map<OutgoingMobilitiesV1HostProvider, Collection<String>> result = new HashMap<>();
    for (String omobilityId : omobilityIds) {
      Optional<EwpOutgoingMobilityMapping> mappingOptional = mappingRepository.findByHeiIdAndOmobilityId(
          heiId, omobilityId);
      if (mappingOptional.isPresent()) {
        EwpOutgoingMobilityMapping mapping = mappingOptional.get();

        Collection<OutgoingMobilitiesV1HostProvider> providers = hostPluginManager.getProvidersByHeiIdAndOunitId(
            heiId, mapping.getOunitId(), OutgoingMobilitiesV1HostProvider.class);
        if (!providers.isEmpty()) {
          OutgoingMobilitiesV1HostProvider provider = providers.iterator().next();
          result.computeIfAbsent(provider, ignored -> new ArrayList<>());
          result.get(provider).add(omobilityId);
        }
      }
    }
    return result;
  }
}
