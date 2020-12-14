package pt.ulisboa.ewp.node.api.host.forward.ewp.controller;

import java.io.Serializable;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiSupportedMajorVersionsResponseDTO;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;

public abstract class AbstractForwardEwpApiController {

  private final RegistryClient registryClient;

  protected AbstractForwardEwpApiController(RegistryClient registryClient) {
    this.registryClient = registryClient;
  }

  @GetMapping("/{heiId}/versions/supported")
  public ResponseEntity<
          ForwardEwpApiResponseWithData<ForwardEwpApiSupportedMajorVersionsResponseDTO>>
      getSupportedVersionsByHeiId(@PathVariable("heiId") String heiId) {
    List<Integer> supportedMajorVersions =
        EwpApiUtils.getSupportedMajorVersions(registryClient, heiId, getApiLocalName());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(
        new ForwardEwpApiSupportedMajorVersionsResponseDTO(supportedMajorVersions));
  }

  protected <T extends Serializable>
      ResponseEntity<ForwardEwpApiResponseWithData<T>> createResponseEntityFromOperationResult(
          EwpSuccessOperationResult<T> successOperationResult) {
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(
        successOperationResult.getResponse(), successOperationResult.getResponseBody());
  }

  public abstract String getApiLocalName();
}
