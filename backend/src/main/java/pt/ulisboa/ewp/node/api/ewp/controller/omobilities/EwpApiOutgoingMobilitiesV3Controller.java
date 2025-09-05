package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesGetResponseV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesIndexResponseV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesUpdateRequestV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesUpdateResponseV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.StudentMobilityV3;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV3HostProvider;
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
@RequestMapping(EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilitiesV3Controller.BASE_PATH)
public class EwpApiOutgoingMobilitiesV3Controller {

  public static final String BASE_PATH = "omobilities/v3";

  private static final Logger LOG =
      LoggerFactory.getLogger(EwpApiOutgoingMobilitiesV3Controller.class);

  private final HostPluginManager hostPluginManager;

  private final EwpOutgoingMobilityMappingRepository mappingRepository;

  public EwpApiOutgoingMobilitiesV3Controller(
      HostPluginManager hostPluginManager, EwpOutgoingMobilityMappingRepository mappingRepository) {
    this.hostPluginManager = hostPluginManager;
    this.mappingRepository = mappingRepository;
  }

  @EwpApiEndpoint(api = "omobilities", apiMajorVersion = 3, endpoint = "index")
  @RequestMapping(
      path = "/{sendingHeiId}/index",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Index API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesIndexResponseV3> outgoingMobilityIds(
      @PathVariable String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, defaultValue = "")
          String receivingAcademicYearId,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
          @DateTimeFormat(iso = DATE_TIME)
          LocalDateTime modifiedSince,
      @RequestParam(value = EwpApiParamConstants.GLOBAL_ID, defaultValue = "") String globalId,
      @RequestParam(value = EwpApiParamConstants.ACTIVITY_ATTRIBUTES, defaultValue = "")
          String activityAttributes,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId, OutgoingMobilitiesV3HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    Collection<OutgoingMobilitiesV3HostProvider> providers =
        hostPluginManager.getAllProvidersOfType(
            sendingHeiId, OutgoingMobilitiesV3HostProvider.class);

    OmobilitiesIndexResponseV3 response = new OmobilitiesIndexResponseV3();
    providers.forEach(
        provider -> {
          Collection<String> outgoingMobilityIds =
              provider.findOutgoingMobilityIds(
                  authenticationToken.getPrincipal().getHeiIdCoveredByClient().orElseThrow(),
                  sendingHeiId,
                  receivingAcademicYearId,
                  modifiedSince,
                  globalId,
                  activityAttributes);
          response.getOmobilityId().addAll(outgoingMobilityIds);
        });
    return ResponseEntity.ok(response);
  }

  @EwpApiEndpoint(api = "omobilities", apiMajorVersion = 3, endpoint = "get")
  @RequestMapping(
      path = "/{sendingHeiId}/get",
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Get API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesGetResponseV3> outgoingMobilities(
      @PathVariable String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID) List<String> outgoingMobilityIds,
      EwpApiHostAuthenticationToken authenticationToken) {

    if (!hostPluginManager.hasHostProvider(sendingHeiId, OutgoingMobilitiesV3HostProvider.class)) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }

    outgoingMobilityIds =
        outgoingMobilityIds != null ? outgoingMobilityIds : Collections.emptyList();

    if (outgoingMobilityIds.isEmpty()) {
      throw new EwpBadRequestException("At least some Outgoing Mobility ID must be provided");
    }

    int maxOmobilityIdsPerRequest =
        hostPluginManager
            .getAllProvidersOfType(sendingHeiId, OutgoingMobilitiesV3HostProvider.class)
            .stream()
            .mapToInt(OutgoingMobilitiesV3HostProvider::getMaxOutgoingMobilityIdsPerRequest)
            .min()
            .orElse(0);

    if (outgoingMobilityIds.size() > maxOmobilityIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid Outgoing Mobility IDs per request is "
              + maxOmobilityIdsPerRequest);
    }

    OmobilitiesGetResponseV3 response = new OmobilitiesGetResponseV3();

    // NOTE: The algorithm handles each Outgoing Mobility ID individually as it may be necessary to
    // fall back to one or more providers.
    for (String outgoingMobilityId : outgoingMobilityIds) {
      List<OutgoingMobilitiesV3HostProvider> providersChain =
          getProvidersChainForHeiAndOutgoingMobilityId(sendingHeiId, outgoingMobilityId);
      for (OutgoingMobilitiesV3HostProvider possibleProvider : providersChain) {
        Collection<StudentMobilityV3> providerResponse =
            possibleProvider.findBySendingHeiIdAndOutgoingMobilityIds(
                authenticationToken.getPrincipal().getHeiIdCoveredByClient().orElseThrow(),
                sendingHeiId,
                List.of(outgoingMobilityId));
        if (!providerResponse.isEmpty()) {
          StudentMobilityV3 mobilityObject = providerResponse.iterator().next();
          response.getSingleMobilityObject().add(mobilityObject);
          break;
        }
      }
    }
    return ResponseEntity.ok(response);
  }

  @EwpApiEndpoint(api = "omobilities", apiMajorVersion = 3, endpoint = "update")
  @RequestMapping(
      path = "/{sendingHeiId}/update",
      method = {RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Update API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesUpdateResponseV3> updateOutgoingMobilities(
      @PathVariable String sendingHeiId,
      @Valid @RequestBody OmobilitiesUpdateRequestV3 updateData,
      EwpApiHostAuthenticationToken authenticationToken) {

    String omobilityId = getOmobilityIdOfUpdateData(updateData);

    Optional<EwpOutgoingMobilityMapping> mappingOptional =
        mappingRepository.findByHeiIdAndOmobilityId(sendingHeiId, omobilityId);
    String ounitIdCoveringOutgoingMobility;
    if (mappingOptional.isPresent()) {
      ounitIdCoveringOutgoingMobility = mappingOptional.get().getOunitId();
    } else {
      LOG.warn(
          "Unknown outgoing mobility with omobility_id '{}', forwarding to primary plugin",
          omobilityId);
      ounitIdCoveringOutgoingMobility = null;
    }

    Optional<OutgoingMobilitiesV3HostProvider> providerOptional =
        hostPluginManager.getSingleProvider(
            sendingHeiId, ounitIdCoveringOutgoingMobility, OutgoingMobilitiesV3HostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + sendingHeiId);
    }
    OutgoingMobilitiesV3HostProvider provider = providerOptional.get();

    OmobilitiesUpdateResponseV3 response =
        provider.updateOutgoingMobility(
            authenticationToken.getPrincipal().getHeiIdCoveredByClient().orElseThrow(),
            sendingHeiId,
            updateData);
    return ResponseEntity.ok(response);
  }

  private String getOmobilityIdOfUpdateData(OmobilitiesUpdateRequestV3 updateData) {
    if (updateData.getApproveProposalV1() != null) {
      return updateData.getApproveProposalV1().getOmobilityId();
    } else {
      return updateData.getRejectProposalV1().getOmobilityId();
    }
  }

  private List<OutgoingMobilitiesV3HostProvider> getProvidersChainForHeiAndOutgoingMobilityId(
      String heiId, String outgoingMobilityId) throws EwpUnknownHeiIdException {

    if (!hostPluginManager.hasHostProvider(heiId, OutgoingMobilitiesV3HostProvider.class)) {
      return new ArrayList<>();
    }

    Optional<EwpOutgoingMobilityMapping> mappingOptional =
        mappingRepository.findByHeiIdAndOmobilityId(heiId, outgoingMobilityId);
    if (mappingOptional.isPresent()) {
      EwpOutgoingMobilityMapping mapping = mappingOptional.get();
      Optional<OutgoingMobilitiesV3HostProvider> providerOptional =
          hostPluginManager.getSingleProvider(
              heiId, mapping.getOunitId(), OutgoingMobilitiesV3HostProvider.class);
      if (providerOptional.isPresent()) {
        OutgoingMobilitiesV3HostProvider provider = providerOptional.get();
        return List.of(provider);
      } else {
        throw new EwpUnknownOrganizationalUnitIdException(heiId, mapping.getOunitId());
      }

    } else {
      return hostPluginManager.getPrimaryFollowedByNonPrimaryProviders(
          heiId, OutgoingMobilitiesV3HostProvider.class);
    }
  }
}
