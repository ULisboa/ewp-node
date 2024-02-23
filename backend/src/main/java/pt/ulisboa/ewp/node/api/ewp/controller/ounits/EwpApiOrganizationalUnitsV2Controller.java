package pt.ulisboa.ewp.node.api.ewp.controller.ounits;

import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2;
import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2.Ounit;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.ounits.OrganizationalUnitsV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApiEndpoint;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownHeiIdException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownOrganizationalUnitCodeException;
import pt.ulisboa.ewp.node.exception.ewp.EwpUnknownOrganizationalUnitIdException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH)
public class EwpApiOrganizationalUnitsV2Controller {

  public static final String BASE_PATH = "ounits/v2";

  private final HostPluginManager hostPluginManager;

  public EwpApiOrganizationalUnitsV2Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @EwpApiEndpoint(api = "ounits", apiMajorVersion = 2)
  @RequestMapping(
      method = {RequestMethod.GET, RequestMethod.POST},
      produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Organizational Units API.",
      tags = {"ewp"})
  public ResponseEntity<OunitsResponseV2> ounits(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId,
      @RequestParam(value = EwpApiParamConstants.OUNIT_ID, required = false) List<String> ounitIds,
      @RequestParam(value = EwpApiParamConstants.OUNIT_CODE, required = false)
          List<String> ounitCodes) {

    ounitIds = ounitIds != null ? ounitIds : Collections.emptyList();
    ounitCodes = ounitCodes != null ? ounitCodes : Collections.emptyList();

    if (!hostPluginManager.hasHostProvider(heiId, OrganizationalUnitsV2HostProvider.class)) {
      throw new EwpUnknownHeiIdException(heiId);
    }

    if (!ounitIds.isEmpty() && !ounitCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "Only organizational unit IDs or codes are accepted, not both simultaneously");
    }

    if (ounitIds.isEmpty() && ounitCodes.isEmpty()) {
      throw new EwpBadRequestException(
          "At least some organizational unit ID or code must be provided");
    }

    if (!ounitIds.isEmpty()) {
      return ounitsByIds(heiId, ounitIds);
    } else {
      return ounitsByCodes(heiId, ounitCodes);
    }
  }

  private ResponseEntity<OunitsResponseV2> ounitsByIds(String heiId, List<String> ounitIds) {
    Map<OrganizationalUnitsV2HostProvider, Collection<String>> providerToOunitIdsMap = hostPluginManager.getOunitIdsCoveredPerProviderOfHeiId(
        heiId, ounitIds, OrganizationalUnitsV2HostProvider.class);

    if (providerToOunitIdsMap.isEmpty()) {
      throw new EwpUnknownOrganizationalUnitIdException(heiId, ounitIds.get(0));
    }

    int maxOunitIdsPerRequest = providerToOunitIdsMap.keySet().stream().mapToInt(
            OrganizationalUnitsV2HostProvider::getMaxOunitIdsPerRequest)
        .min().orElse(0);

    if (ounitIds.size() > maxOunitIdsPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid organizational unit IDs per request is "
              + maxOunitIdsPerRequest);
    }

    Map<String, Ounit> ounitIdToOunitMap = new HashMap<>();
    for (Map.Entry<OrganizationalUnitsV2HostProvider, Collection<String>> entry : providerToOunitIdsMap.entrySet()) {
      OrganizationalUnitsV2HostProvider provider = entry.getKey();
      Collection<String> coveredOunitIds = entry.getValue();
      provider.findByHeiIdAndOunitIds(heiId, coveredOunitIds)
          .forEach(ounit -> ounitIdToOunitMap.put(ounit.getOunitId(), ounit));
    }

    OunitsResponseV2 response = new OunitsResponseV2();
    for (String ounitId : ounitIds) {
      if (ounitIdToOunitMap.containsKey(ounitId)) {
        response.getOunit().add(ounitIdToOunitMap.get(ounitId));
      }
    }
    return ResponseEntity.ok(response);
  }

  private ResponseEntity<OunitsResponseV2> ounitsByCodes(String heiId, List<String> ounitCodes) {
    Map<OrganizationalUnitsV2HostProvider, Collection<String>> providerToOunitCodesMap = hostPluginManager.getOunitCodesCoveredPerProviderOfHeiId(
        heiId, ounitCodes, OrganizationalUnitsV2HostProvider.class);

    if (providerToOunitCodesMap.isEmpty()) {
      throw new EwpUnknownOrganizationalUnitCodeException(heiId, ounitCodes.get(0));
    }

    int maxOunitCodesPerRequest = providerToOunitCodesMap.keySet().stream().mapToInt(
            OrganizationalUnitsV2HostProvider::getMaxOunitCodesPerRequest)
        .min().orElse(0);

    if (ounitCodes.size() > maxOunitCodesPerRequest) {
      throw new EwpBadRequestException(
          "Maximum number of valid organizational unit codes per request is "
              + maxOunitCodesPerRequest);
    }

    Map<String, Ounit> ounitCodeToOunitMap = new HashMap<>();
    for (Map.Entry<OrganizationalUnitsV2HostProvider, Collection<String>> entry : providerToOunitCodesMap.entrySet()) {
      OrganizationalUnitsV2HostProvider provider = entry.getKey();
      Collection<String> coveredOunitCodes = entry.getValue();
      provider.findByHeiIdAndOunitCodes(heiId, coveredOunitCodes)
          .forEach(ounit -> ounitCodeToOunitMap.put(ounit.getOunitCode(), ounit));
    }

    OunitsResponseV2 response = new OunitsResponseV2();
    for (String ounitCode : ounitCodes) {
      if (ounitCodeToOunitMap.containsKey(ounitCode)) {
        response.getOunit().add(ounitCodeToOunitMap.get(ounitCode));
      }
    }
    return ResponseEntity.ok(response);
  }
}
