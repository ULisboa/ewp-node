package pt.ulisboa.ewp.node.api.ewp.controller.factsheets;

import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetResponseV1;
import io.swagger.v3.oas.annotations.Operation;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.factsheets.FactSheetsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + EwpApiFactSheetsV1Controller.BASE_PATH)
public class EwpApiFactSheetsV1Controller {

  public static final String BASE_PATH = "factsheets/v1";

  private final HostPluginManager hostPluginManager;

  public EwpApiFactSheetsV1Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @RequestMapping(method = {RequestMethod.GET,
      RequestMethod.POST}, produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Fact Sheets API.",
      tags = {"ewp"})
  public ResponseEntity<FactsheetResponseV1> factSheets(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "") String heiId) {

    Optional<FactsheetResponseV1.Factsheet> factSheetOptional = getHostProvider(
        heiId).findByHeiId(heiId);
    FactsheetResponseV1 response = new FactsheetResponseV1();
    factSheetOptional.ifPresent(factSheet -> response.getFactsheet().add(factSheet));
    return ResponseEntity.ok(response);
  }

  private FactSheetsV1HostProvider getHostProvider(String heiId) {
    Optional<FactSheetsV1HostProvider> providerOptional =
        hostPluginManager
            .getPrimaryProvider(heiId, FactSheetsV1HostProvider.class);
    if (providerOptional.isEmpty()) {
      throw new EwpBadRequestException("Unknown HEI ID: " + heiId);
    }
    return providerOptional.get();
  }
}
