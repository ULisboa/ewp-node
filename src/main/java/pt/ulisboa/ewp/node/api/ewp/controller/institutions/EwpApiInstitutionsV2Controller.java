package pt.ulisboa.ewp.node.api.ewp.controller.institutions;

import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2;
import io.swagger.v3.oas.annotations.Operation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.institutions.InstitutionsV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + EwpApiInstitutionsV2Controller.BASE_PATH)
public class EwpApiInstitutionsV2Controller {

  public static final String BASE_PATH = "institutions/v2";

  private final HostPluginManager hostPluginManager;

  public EwpApiInstitutionsV2Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Institutions API.",
      tags = {"ewp"})
  public ResponseEntity<InstitutionsResponseV2> institutionsGet(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, defaultValue = "")
          List<String> heiIds) {
    return institutions(heiIds);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Institutions API.",
      tags = {"ewp"})
  public ResponseEntity<InstitutionsResponseV2> institutionsPost(
      @RequestParam(value = EwpApiParamConstants.HEI_ID, required = false)
          List<String> heiIds) {
    if (heiIds == null) {
      heiIds = new ArrayList<>();
    }
    return institutions(heiIds);
  }

  private ResponseEntity<InstitutionsResponseV2> institutions(List<String> heiIds) {
    if (heiIds.isEmpty()) {
      throw new EwpBadRequestException("At least one valid HEI ID must be provided");
    }

    if (heiIds.size() > EwpApiConstants.MAX_HEI_IDS) {
      throw new EwpBadRequestException(
          "Maximum number of valid HEI IDs per request is " + EwpApiConstants.MAX_HEI_IDS);
    }

    Map<String, InstitutionsV2HostProvider> heiIdToProviderMap =
        hostPluginManager.getProviderPerHeiId(heiIds, InstitutionsV2HostProvider.class);

    InstitutionsResponseV2 response = new InstitutionsResponseV2();
    heiIdToProviderMap.entrySet().stream()
        .map(entry -> entry.getValue().findByHeiId(entry.getKey()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(h -> response.getHei().add(h));
    return ResponseEntity.ok(response);
  }
}
