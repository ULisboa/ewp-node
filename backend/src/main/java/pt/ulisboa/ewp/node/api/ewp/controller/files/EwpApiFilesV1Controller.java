package pt.ulisboa.ewp.node.api.ewp.controller.files;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import java.util.Optional;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.FileResponse;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.files.FilesV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.controller.EwpApi;
import pt.ulisboa.ewp.node.api.ewp.security.EwpApiHostAuthenticationToken;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.exception.ewp.EwpNotFoundException;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;

@RestController
@EwpApi
@RequestMapping(
    EwpApiConstants.API_BASE_URI + EwpApiFilesV1Controller.BASE_PATH)
public class EwpApiFilesV1Controller {

  public static final String BASE_PATH = "files/v1";

  private final HostPluginManager hostPluginManager;

  public EwpApiFilesV1Controller(HostPluginManager hostPluginManager) {
    this.hostPluginManager = hostPluginManager;
  }

  @RequestMapping(
      path = "/{heiId}",
      method = {RequestMethod.GET})
  @Operation(
      summary = "Files API.",
      tags = {"ewp"})
  public ResponseEntity<byte[]> getFile(
      @PathVariable String heiId,
      @RequestParam(EwpApiParamConstants.FILE_ID) String fileId,
      EwpApiHostAuthenticationToken authenticationToken) {

    Collection<FilesV1HostProvider> filesHostProviders = this.hostPluginManager.getAllProvidersOfType(
        heiId, FilesV1HostProvider.class);

    Optional<FileResponse> fileResponseOptional = getFile(fileId,
        authenticationToken, filesHostProviders);
    if (fileResponseOptional.isEmpty()) {
      throw new EwpNotFoundException("File ID is unknown: " + fileId);
    }

    FileResponse fileResponse = fileResponseOptional.get();
    return ResponseEntity.ok()
        .contentType(MediaType.parseMediaType(fileResponse.getMediaType()))
        .body(fileResponse.getData());
  }

  private static Optional<FileResponse> getFile(String fileId,
      EwpApiHostAuthenticationToken authenticationToken,
      Collection<FilesV1HostProvider> filesHostProviders) {
    for (FilesV1HostProvider filesHostProvider : filesHostProviders) {
      Optional<FileResponse> fileResponseOptional = filesHostProvider.getFile(
          authenticationToken.getPrincipal().getHeiIdsCoveredByClient(), fileId);

      if (fileResponseOptional.isPresent()) {
        return fileResponseOptional;
      }
    }
    return Optional.empty();
  }
}
