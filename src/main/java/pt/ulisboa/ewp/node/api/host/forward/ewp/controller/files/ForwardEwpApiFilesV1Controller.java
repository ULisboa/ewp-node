package pt.ulisboa.ewp.node.api.host.forward.ewp.controller.files;

import io.swagger.v3.oas.annotations.Operation;
import javax.validation.Valid;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.FileResponse;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.AbstractForwardEwpApiController;
import pt.ulisboa.ewp.node.api.host.forward.ewp.controller.ForwardEwpApi;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.ForwardEwpApiResponseWithData;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.files.FileRequestDto;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.ForwardEwpApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.utils.ForwardEwpApiResponseUtils;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.files.EwpFilesV1Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.utils.EwpApi;

@RestController
@ForwardEwpApi(EwpApi.FILES)
@RequestMapping(ForwardEwpApiConstants.API_BASE_URI + "files/v1")
@Secured({ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX})
public class ForwardEwpApiFilesV1Controller extends AbstractForwardEwpApiController {

  private final EwpFilesV1Client client;

  public ForwardEwpApiFilesV1Controller(
      RegistryClient registryClient, EwpFilesV1Client client) {
    super(registryClient);
    this.client = client;
  }

  @GetMapping
  @Operation(
      summary = "EWP Files Forward API.",
      tags = {"Files"})
  public ResponseEntity<ForwardEwpApiResponseWithData<FileResponse>> getFile(
      @Valid @ParameterObject @RequestParam FileRequestDto requestDto)
      throws EwpClientErrorException {

    EwpSuccessOperationResult<FileResponse> fileResponse = client.getFile(requestDto.getHeiId(),
        requestDto.getFileId());
    return ForwardEwpApiResponseUtils.toSuccessResponseEntity(fileResponse);
  }
}
