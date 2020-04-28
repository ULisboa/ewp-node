package pt.ulisboa.ewp.node.api.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.io.IOException;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import pt.ulisboa.ewp.node.api.admin.annotation.AdminApiWithResponseBodyWrapper;
import pt.ulisboa.ewp.node.api.admin.security.AdminApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiConstants;
import pt.ulisboa.ewp.node.api.common.dto.ApiOperationStatusDTO;
import pt.ulisboa.ewp.node.service.keystore.KeyStoreService;

@RestController
@AdminApi
@RequestMapping(AdminApiConstants.API_BASE_URI + "keystore")
@Secured({AdminApiSecurityCommonConstants.ROLE_ADMIN_WITH_PREFIX})
@AdminApiWithResponseBodyWrapper
@Validated
public class AdminApiKeyStoreController extends AbstractAdminApiController {

  @Autowired private Logger log;

  @Autowired private KeyStoreService keyStoreService;

  @PostMapping(
      consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Changes the keystore used for EWP communications.",
      tags = {"admin"})
  @ApiResponses({
    @ApiResponse(
        responseCode = "200",
        description = "New keystore persisted successfully",
        content = @Content(schema = @Schema(implementation = ApiOperationStatusDTO.class))),
    @ApiResponse(
        responseCode = "400",
        description = "Request is not valid",
        content = @Content(schema = @Schema(implementation = ApiOperationStatusDTO.class)))
  })
  public ResponseEntity<ApiOperationStatusDTO> changeKeyStore(
      @RequestPart("certificateFile") MultipartFile certificateFile,
      @RequestPart("privateKeyFile") MultipartFile privateKeyFile) {
    try {
      boolean success =
          keyStoreService.persistKeystoreWithCertificateAndKey(
              certificateFile.getBytes(), privateKeyFile.getBytes());
      return getOperationApiResponse(success, "error.keystore.persist");
    } catch (IOException e) {
      log.error("Failed to persist keystore", e);
      return ResponseEntity.badRequest().body(new ApiOperationStatusDTO(false));
    }
  }
}
