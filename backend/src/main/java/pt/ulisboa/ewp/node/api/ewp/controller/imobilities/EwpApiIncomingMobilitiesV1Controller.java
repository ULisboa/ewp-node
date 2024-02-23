package pt.ulisboa.ewp.node.api.ewp.controller.imobilities;

import eu.erasmuswithoutpaper.api.imobilities.v1.endpoints.ImobilitiesGetResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.IncomingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
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
    EwpApiConstants.API_BASE_URI + EwpApiIncomingMobilitiesV1Controller.BASE_PATH)
public class EwpApiIncomingMobilitiesV1Controller {

  public static final String BASE_PATH = "imobilities/v1";

  private final HostPluginManager hostPluginManager;

  private final EwpOutgoingMobilityMappingRepository mappingRepository;

  public EwpApiIncomingMobilitiesV1Controller(HostPluginManager hostPluginManager,
      EwpOutgoingMobilityMappingRepository mappingRepository) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
  }

  @EwpApiEndpoint(api = "imobilities", apiMajorVersion = 1, endpoint = "get")
  @RequestMapping(
      path = "/get",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Incoming Mobilities Get API.",
      tags = {"ewp"})
  public ResponseEntity<ImobilitiesGetResponseV1> incomingMobilities(
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID) String receivingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> outgoingMobilityIds,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(receivingHeiId,
        IncomingMobilitiesV1HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + receivingHeiId);
    }

    outgoingMobilityIds =
        outgoingMobilityIds != null ? outgoingMobilityIds : Collections.emptyList();

    if (outgoingMobilityIds.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some Outgoing Mobility ID must be provided");
    }

    int maxOmobilityIdsPerRequest = hostPluginManager.getAllProvidersOfType(receivingHeiId,
            IncomingMobilitiesV1HostProvider.class).stream().mapToInt(
            IncomingMobilitiesV1HostProvider::getMaxOutgoingMobilityIdsPerRequest)
        .min().orElse(0);

    if (outgoingMobilityIds.size() > maxOmobilityIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid Outgoing Mobility IDs per request is "
              + maxOmobilityIdsPerRequest);
    }

    Map<IncomingMobilitiesV1HostProvider, Collection<String>> providerToOmobilityIdsMap = getOmobilityIdsCoveredPerProviderOfHeiId(
        receivingHeiId, outgoingMobilityIds);

    ImobilitiesGetResponseV1 response = new ImobilitiesGetResponseV1();
    for (Map.Entry<IncomingMobilitiesV1HostProvider, Collection<String>> entry : providerToOmobilityIdsMap.entrySet()) {
      IncomingMobilitiesV1HostProvider provider = entry.getKey();
      Collection<String> coveredOmobilityIds = entry.getValue();
      provider.findByReceivingHeiIdAndOutgoingMobilityIds(
              authenticationToken.getPrincipal().getHeiIdsCoveredByClient(), receivingHeiId,
              coveredOmobilityIds)
          .forEach(mobility -> response.getSingleIncomingMobilityObject().add(mobility));
    }
    return ResponseEntity.ok(response);
  }

  private Map<IncomingMobilitiesV1HostProvider, Collection<String>> getOmobilityIdsCoveredPerProviderOfHeiId(
      String heiId, Collection<String> omobilityIds) {
    Map<IncomingMobilitiesV1HostProvider, Collection<String>> result = new HashMap<>();
    for (String omobilityId : omobilityIds) {
      Optional<EwpOutgoingMobilityMapping> mappingOptional = mappingRepository.findByHeiIdAndOmobilityId(
          heiId, omobilityId);
      if (mappingOptional.isPresent()) {
        EwpOutgoingMobilityMapping mapping = mappingOptional.get();

        Optional<IncomingMobilitiesV1HostProvider> providerOptional = hostPluginManager.getSingleProvider(
            heiId, mapping.getOunitId(), IncomingMobilitiesV1HostProvider.class);
        if (providerOptional.isPresent()) {
          IncomingMobilitiesV1HostProvider provider = providerOptional.get();
          result.computeIfAbsent(provider, ignored -> new ArrayList<>());
          result.get(provider).add(omobilityId);
        }
      }
    }
    return result;
  }
}
