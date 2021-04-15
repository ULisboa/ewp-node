package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import java.util.List;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiSupportedMajorVersionsResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.validation.annotation.ValidEwpApiLocalName;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI)
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
@Validated
public class ForwardEwpApiGeneralController {

  private final RegistryClient registryClient;

  protected ForwardEwpApiGeneralController(RegistryClient registryClient) {
    this.registryClient = registryClient;
  }

  @GetMapping("/apis/{heiId}/versions/supported")
  public ResponseEntity<
      ForwardEwpApiResponseWithData<ForwardEwpApiSupportedMajorVersionsResponseDTO>>
  getSupportedVersionsByHeiId(@PathVariable("heiId") String heiId,
      @Valid @ValidEwpApiLocalName @RequestParam("api") String apiLocalName) {
    List<Integer> supportedMajorVersions =
        EwpApiUtils.getSupportedMajorVersions(registryClient, heiId, apiLocalName);
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(
        new ForwardEwpApiSupportedMajorVersionsResponseDTO(supportedMajorVersions));
  }
}
