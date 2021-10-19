package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import static org.springframework.format.annotation.DateTimeFormat.ISO.DATE_TIME;

import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesIndexResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH)
public class EwpApiOutgoingMobilitiesV1Controller {

  public static final String BASE_PATH = "omobilities/v1";

  private final HostPluginManager hostPluginManager;

  public EwpApiOutgoingMobilitiesV1Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @GetMapping(value = "/index", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Index API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesIndexResponseV1> outgoingMobilityIdsGet(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID) String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID, required = false) Collection<String> receivingHeiIds,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, defaultValue = "") String receivingAcademicYearId,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
      @DateTimeFormat(iso = DATE_TIME) LocalDateTime modifiedSince) {

    Collection<String> outgoingMobilityIds = getHostProvider(sendingHeiId)
        .findOutgoingMobilityIds(sendingHeiId, receivingHeiIds, receivingAcademicYearId,
            modifiedSince);
    OmobilitiesIndexResponseV1 response = new OmobilitiesIndexResponseV1();
    response.getOmobilityId().addAll(outgoingMobilityIds);
    return ResponseEntity.ok(response);
  }

  @PostMapping(value = "/index", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Index API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesIndexResponseV1> outgoingMobilityIdsPost(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID, defaultValue = "") String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_HEI_ID, required = false) Collection<String> receivingHeiIds,
      @RequestParam(value = EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, defaultValue = "") String receivingAcademicYearId,
      @RequestParam(value = EwpApiParamConstants.MODIFIED_SINCE, required = false)
      @DateTimeFormat(iso = DATE_TIME) LocalDateTime modifiedSince) {

    Collection<String> outgoingMobilityIds = getHostProvider(sendingHeiId)
        .findOutgoingMobilityIds(sendingHeiId, receivingHeiIds, receivingAcademicYearId,
            modifiedSince);
    OmobilitiesIndexResponseV1 response = new OmobilitiesIndexResponseV1();
    response.getOmobilityId().addAll(outgoingMobilityIds);
    return ResponseEntity.ok(response);
  }

  @GetMapping(value = "/get", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Get API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesGetResponseV1> outgoingMobilitiesGet(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID, defaultValue = "") String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID)
          List<String> outgoingMobilityIds) {
    return outgoingMobilities(sendingHeiId, outgoingMobilityIds);
  }

  @PostMapping(value = "/get", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Outgoing Mobilities Get API.",
      tags = {"ewp"})
  public ResponseEntity<OmobilitiesGetResponseV1> outgoingMobilitiesPost(
      @RequestParam(value = EwpApiParamConstants.SENDING_HEI_ID, defaultValue = "") String sendingHeiId,
      @RequestParam(value = EwpApiParamConstants.OMOBILITY_ID)
          List<String> outgoingMobilityIds) {
    return outgoingMobilities(sendingHeiId, outgoingMobilityIds);
  }

  private ResponseEntity<OmobilitiesGetResponseV1> outgoingMobilities(String sendingHeiId,
      List<String> outgoingMobilityIds) {
    outgoingMobilityIds =
        outgoingMobilityIds != null ? outgoingMobilityIds : Collections.emptyList();

    OutgoingMobilitiesV1HostProvider provider = getHostProvider(sendingHeiId);

    if (outgoingMobilityIds.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some Outgoing Mobility ID or code must be provided");
    }

    if (outgoingMobilityIds.size() > provider.getMaxOutgoingMobilityIdsPerRequest()) {
      throw new EwpBadRequestException(
          "Maximum number of valid Outgoing Mobility IDs per request is "
              + provider.getMaxOutgoingMobilityIdsPerRequest());
    }

    OmobilitiesGetResponseV1 response = new OmobilitiesGetResponseV1();
    response.getSingleMobilityObject()
        .addAll(
            provider.findBySendingHeiIdAndOutgoingMobilityIds(sendingHeiId, outgoingMobilityIds));
    return ResponseEntity.ok(response);
  }

  private OutgoingMobilitiesV1HostProvider getHostProvider(String heiId) {
    Optional<OutgoingMobilitiesV1HostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, OutgoingMobilitiesV1HostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }
    return providerOptional.get();
  }
}
