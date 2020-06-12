package pt.ulisboa.ewp.node.api.ewp.controller.institutions;

import eu.erasmuswithoutpaper.api.institutions.InstitutionsResponse;
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
import pt.ulisboa.ewp.host.plugin.skeleton.provider.InstitutionsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpBadRequestException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(EwpApiConstants.API_BASE_URI + "institutions")
public class EwpApiInstitutionsController {

  private final HostPluginManager hostPluginManager;

  public EwpApiInstitutionsController(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @GetMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Institutions API.",
      tags = {"ewp"})
  public ResponseEntity<InstitutionsResponse> institutionsGet(
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_HEI_ID, defaultValue = "")
          List<String> heiIds) {
    return institutions(heiIds);
  }

  @PostMapping(produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "Institutions API.",
      tags = {"ewp"})
  public ResponseEntity<InstitutionsResponse> institutionsPost(
      @RequestParam(value = EwpApiParamConstants.PARAM_NAME_HEI_ID, required = false)
          List<String> heiIds) {
    if (heiIds == null) {
      heiIds = new ArrayList<>();
    }
    return institutions(heiIds);
  }

  private ResponseEntity<InstitutionsResponse> institutions(List<String> heiIds) {
    Map<String, InstitutionsHostProvider> heiIdToProviderMap =
        hostPluginManager.getProviderPerHeiId(heiIds, InstitutionsHostProvider.class);
    if (heiIdToProviderMap.size() > EwpApiConstants.MAX_HEI_IDS) {
      throw new EwpBadRequestException(
          "Maximum number of valid HEI IDs per request is " + EwpApiConstants.MAX_HEI_IDS);
    }

    InstitutionsResponse response = new InstitutionsResponse();
    heiIdToProviderMap.entrySet().stream()
        .map(entry -> entry.getValue().findByHeiId(entry.getKey()))
        .filter(Optional::isPresent)
        .map(Optional::get)
        .forEach(h -> response.getHei().add(h));
    return ResponseEntity.ok(response);
  }
}
