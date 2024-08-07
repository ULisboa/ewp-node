package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.OmobilitiesGetResponseV2;
import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.OmobilitiesIndexResponseV2;
import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.StudentMobilityV2;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
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
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpOutgoingMobilityMappingRepository;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownOrganizationalUnitIdException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilitiesV2Controller.BASE_PATH)
public class EwpApiOutgoingMobilitiesV2Controller {

  public static final String BASE_PATH = "omobilities/v2";

  private final HostPluginManager hostPluginManager;

  private final EwpOutgoingMobilityMappingRepository mappingRepository;

  public EwpApiOutgoingMobilitiesV2Controller(
      HostPluginManager hostPluginManager, EwpOutgoingMobilityMappingRepository mappingRepository) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
  }

  @EwpApiEndpoint(api = "omobilities", apiMajorVersion = 2, endpoint = "index")
  @RequestMapping(
      path = "/index",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Index API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesIndexResponseV2> outgoingMobilityIds(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID) String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID, required = false)
          Collection<String> receivingHeiIds,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, defaultValue = "")
          String receivingAcademicYearId,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
          @DateTimeFormat(iso = DATE_TIME)
          LocalDateTime modifiedSince,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId, OutgoingMobilitiesV2HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    Collection<OutgoingMobilitiesV2HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(
            sendingHeiId, OutgoingMobilitiesV2HostProvider.class);

    OmobilitiesIndexResponseV2 response = new OmobilitiesIndexResponseV2();
    providers.forEach(
        provider -> {
          Collection<String> outgoingMobilityIds =
              provider.findOutgoingMobilityIds(
                  authenticationToken.getPrincipal().getHeiIdsCoveredByClient(),
                  sendingHeiId,
                  receivingHeiIds,
                  receivingAcademicYearId,
                  modifiedSince);
          response.getOmobilityId().addAll(outgoingMobilityIds);
        });
    return ResponseEntity.ok(response);
  }

  @EwpApiEndpoint(api = "omobilities", apiMajorVersion = 2, endpoint = "get")
  @RequestMapping(
      path = "/get",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Get API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesGetResponseV2> outgoingMobilities(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID, defaultValue = "")
          String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> outgoingMobilityIds,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId, OutgoingMobilitiesV2HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    outgoingMobilityIds =
        outgoingMobilityIds != null ? outgoingMobilityIds : Collections.emptyList();

    if (outgoingMobilityIds.isEmpty()) {
      throw new EwpBadRequestException("At least some Outgoing Mobility ID must be provided");
    }

    int maxOmobilityIdsPerRequest =
        hostPluginManager
            .getAllProvidersOfType(sendingHeiId, OutgoingMobilitiesV2HostProvider.class)
            .stream()
            .mapToInt(OutgoingMobilitiesV2HostProvider::getMaxOutgoingMobilityIdsPerRequest)
            .min()
            .orElse(0);

    if (outgoingMobilityIds.size() > maxOmobilityIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid Outgoing Mobility IDs per request is "
              + maxOmobilityIdsPerRequest);
    }

    OmobilitiesGetResponseV2 response = new OmobilitiesGetResponseV2();

    // NOTE: The algorithm handles each Outgoing Mobility ID individually as it may be necessary to
    // fall back to one or more providers.
    for (String outgoingMobilityId : outgoingMobilityIds) {
      List<OutgoingMobilitiesV2HostProvider> providersChain =
          getProvidersChainForHeiAndOutgoingMobilityId(sendingHeiId, outgoingMobilityId);
      for (OutgoingMobilitiesV2HostProvider possibleProvider : providersChain) {
        Collection<StudentMobilityV2> providerResponse =
            possibleProvider.findBySendingHeiIdAndOutgoingMobilityIds(
                authenticationToken.getPrincipal().getHeiIdsCoveredByClient(),
                sendingHeiId,
                List.of(outgoingMobilityId));
        if (!providerResponse.isEmpty()) {
          StudentMobilityV2 mobilityObject = providerResponse.iterator().next();
          response.getSingleMobilityObject().add(mobilityObject);
          break;
        }
      }
    }
    return ResponseEntity.ok(response);
  }

  private List<OutgoingMobilitiesV2HostProvider> getProvidersChainForHeiAndOutgoingMobilityId(
      String heiId, String outgoingMobilityId) throws EwpUnknownHeiIdException {

    if (!hostPluginManager.hasHostProvider(heiId, OutgoingMobilitiesV2HostProvider.class)) {
      return new ArrayList<>();
    }

    Optional<EwpOutgoingMobilityMapping> mappingOptional =
        mappingRepository.findByHeiIdAndOmobilityId(heiId, outgoingMobilityId);
    if (mappingOptional.isPresent()) {
      EwpOutgoingMobilityMapping mapping = mappingOptional.get();
      Optional<OutgoingMobilitiesV2HostProvider> providerOptional =
          hostPluginManager.getSingleProvider(
              heiId, mapping.getOunitId(), OutgoingMobilitiesV2HostProvider.class);
      if (providerOptional.isPresent()) {
        OutgoingMobilitiesV2HostProvider provider = providerOptional.get();
        return List.of(provider);
      } else {
        throw new EwpUnknownOrganizationalUnitIdException(heiId, mapping.getOunitId());
      }

    } else {
      return hostPluginManager.getPrimaryFollowedByNonPrimaryProviders(
          heiId, OutgoingMobilitiesV2HostProvider.class);
    }
  }
}
