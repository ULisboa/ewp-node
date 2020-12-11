package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.iias;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiSupportedMajorVersionsResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.client.ewp.utils.EwpClientConstants;

@RestController
@ForwardEwpApi
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "iias")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiInterInstitutionalAgreementsGeneralController
    extends AbstractForwardEwpApiInterInstitutionalAgreementsController {

  private final RegistryClient registryClient;

  public ForwardEwpApiInterInstitutionalAgreementsGeneralController(RegistryClient registryClient) {
    this.registryClient = registryClient;
  }

  @GetMapping("/{heiId}/versions/supported")
  public ResponseEntity<
          ForwardEwpApiResponseWithData<ForwardEwpApiSupportedMajorVersionsResponseDTO>>
      getSupportedVersionsByHeiId(@PathVariable("heiId") String heiId) {
    List<Integer> supportedMajorVersions =
        EwpApiUtils.getSupportedMajorVersions(
            registryClient, heiId, EwpClientConstants.API_INTERINSTITUTIONAL_AGREEMENTS_NAME);
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(
        new ForwardEwpApiSupportedMajorVersionsResponseDTO(supportedMajorVersions));
  }
}
