package pt.ulisboa.ewp.node.api.ewp.controller.ounits;

import eu.erasmuswithoutpaper.api.ounits.OunitsResponse;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.OrganizationalUnitsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + "ounits")
public class EwpApiOrganizationalUnitsController {

  private final HostPluginManager hostPluginManager;

  public EwpApiOrganizationalUnitsController(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Organizational Units API.",
      tags = {"ewp"})
  public ResponseEntity<OunitsResponse> ounitsGet(
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_OUNIT_ID, required = false)
          List<String> ounitIds,
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_OUNIT_CODE, required = false)
          List<String> ounitCodes) {
    ounitIds = ounitIds != null ? ounitIds : Collections.emptyList();
    ounitCodes = ounitCodes != null ? ounitCodes : Collections.emptyList();
    return ounits(heiId, ounitIds, ounitCodes);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Organizational Units API.",
      tags = {"ewp"})
  public ResponseEntity<OunitsResponse> ounitsPost(
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_OUNIT_ID, required = false)
          List<String> ounitIds,
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_OUNIT_CODE, required = false)
          List<String> ounitCodes) {
    ounitIds = ounitIds != null ? ounitIds : Collections.emptyList();
    ounitCodes = ounitCodes != null ? ounitCodes : Collections.emptyList();
    return ounits(heiId, ounitIds, ounitCodes);
  }

  private ResponseEntity<OunitsResponse> ounits(
      String heiId, List<String> ounitIds, List<String> ounitCodes) {
    Optional<OrganizationalUnitsHostProvider> providerOptional =
        hostPluginManager.getProvider(heiId, OrganizationalUnitsHostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }
    OrganizationalUnitsHostProvider provider = providerOptional.get();

    if (!ounitIds.isEmpty() && !ounitCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "Only organizational unit IDs or codes are accepted, not both simultaneously");
    }

    if (ounitIds.isEmpty() && ounitCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some organizational unit ID or code must be provided");
    }

    if (!ounitIds.isEmpty()) {
      return ounitsByIds(provider, heiId, ounitIds);
    } else {
      return ounitsByCodes(provider, heiId, ounitCodes);
    }
  }

  private ResponseEntity<OunitsResponse> ounitsByIds(
      OrganizationalUnitsHostProvider provider, String heiId, List<String> ounitIds) {
    if (ounitIds.size() > provider.getMaxOunitIdsPerRequest()) {
      throw new EwpBadRequestException(
          "Maximum number of valid organizational unit IDs per request is "
              + provider.getMaxOunitIdsPerRequest());
    }

    OunitsResponse response = new OunitsResponse();
    response.getOunit().addAll(provider.findByHeiIdAndOunitIds(heiId, ounitIds));
    return ResponseEntity.ok(response);
  }

  private ResponseEntity<OunitsResponse> ounitsByCodes(
      OrganizationalUnitsHostProvider provider, String heiId, List<String> ounitCodes) {
    if (ounitCodes.size() > provider.getMaxOunitCodesPerRequest()) {
      throw new EwpBadRequestException(
          "Maximum number of valid organizational unit codes per request is "
              + provider.getMaxOunitCodesPerRequest());
    }

    OunitsResponse response = new OunitsResponse();
    response.getOunit().addAll(provider.findByHeiIdAndOunitCodes(heiId, ounitCodes));
    return ResponseEntity.ok(response);
  }
}
