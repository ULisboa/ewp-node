package pt.ulisboa.ewp.node.api.ewp.controller.factsheets;

import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.FactSheetsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + "factsheets")
public class EwpApiFactSheetsController {

  private final HostPluginManager hostPluginManager;

  public EwpApiFactSheetsController(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Fact Sheets API.",
      tags = {"ewp"})
  public ResponseEntity<FactsheetResponseV1> factSheetsGet(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId) {

    return ResponseEntity.ok(getFactSheet(heiId));
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Fact Sheets API.",
      tags = {"ewp"})
  public ResponseEntity<FactsheetResponseV1> factSheetsPost(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId) {

    return ResponseEntity.ok(getFactSheet(heiId));
  }

  private FactsheetResponseV1 getFactSheet(String heiId) {
    Optional<FactsheetResponseV1.Factsheet> factSheetOptional = getHostProvider(
        heiId).findByHeiId(heiId);
    FactsheetResponseV1 response = new FactsheetResponseV1();
    factSheetOptional.ifPresent(factSheet -> response.getFactsheet().add(factSheet));
    return response;
  }

  private FactSheetsHostProvider getHostProvider(String heiId) {
    Optional<FactSheetsHostProvider> providerOptional =
        hostPluginManager
            .getProvider(heiId, FactSheetsHostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }
    return providerOptional.get();
  }
}
