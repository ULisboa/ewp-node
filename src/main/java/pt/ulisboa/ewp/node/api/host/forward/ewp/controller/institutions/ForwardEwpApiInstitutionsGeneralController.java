package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.institutions;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiHeiIdsResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

@RestController
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "institutions")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_CLIENT_WITH_PREFIX})
@Validated
public class ForwardEwpApiInstitutionsGeneralController {

  private final RegistryClient registryClient;

  protected ForwardEwpApiInstitutionsGeneralController(RegistryClient registryClient) {
    this.registryClient = registryClient;
  }

  @GetMapping(value = "/hei-ids", produces = MediaType.APPLICATION_XML_VALUE)
  @Operation(
      summary = "EWP Institutions Forward API.",
      tags = {"Institutions"})
  public ResponseEntity<ForwardEwpApiResponseWithData<ForwardEwpApiHeiIdsResponseDTO>>
  getAllHeiIds() {
    Collection<String> heiIds = registryClient.getAllHeiIds();
    return ResponseEntity.ok(
        ForwardEwpApiResponseUtils.createResponseWithMessagesAndData(
            new ForwardEwpApiHeiIdsResponseDTO(heiIds)));
  }
}
