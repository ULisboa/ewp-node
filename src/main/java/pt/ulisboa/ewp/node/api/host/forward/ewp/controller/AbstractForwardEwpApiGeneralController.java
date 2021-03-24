package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiSupportedMajorVersionsResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

public abstract class AbstractForwardEwpApiGeneralController extends
    AbstractForwardEwpApiController {

  protected AbstractForwardEwpApiGeneralController(RegistryClient registryClient) {
    super(registryClient);
  }

  @GetMapping("/{heiId}/versions/supported")
  public ResponseEntity<
      ForwardEwpApiResponseWithData<ForwardEwpApiSupportedMajorVersionsResponseDTO>>
  getSupportedVersionsByHeiId(@PathVariable("heiId") String heiId) {
    List<Integer> supportedMajorVersions =
        EwpApiUtils.getSupportedMajorVersions(getRegistryClient(), heiId, getApiLocalName());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(
        new ForwardEwpApiSupportedMajorVersionsResponseDTO(supportedMajorVersions));
  }

  public abstract String getApiLocalName();
}
