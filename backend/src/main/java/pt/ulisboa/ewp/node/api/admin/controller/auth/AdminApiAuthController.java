package pt.ulisboa.ewp.node.api.admin.controller.auth;

import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.admin.controller.AdminApi;
import pt.ulisboa.ewp.node.api.admin.dto.response.AdminApiResponseWithDataDto;
import pt.ulisboa.ewp.node.api.admin.dto.response.auth.AdminApiUserDto;
import pt.ulisboa.ewp.node.api.admin.security.AdminApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiConstants;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiResponseUtils;

@AdminApi
@RestController
@RequestMapping(AdminApiConstants.API_BASE_URI + "auth")
@Secured({AdminApiSecurityCommonConstants.ROLE_ADMIN_WITH_PREFIX})
@Validated
public class AdminApiAuthController {

  @GetMapping(path = "/user", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(
      summary = "Returns current user authentication data.",
      tags = {"Admin"})
  public ResponseEntity<AdminApiResponseWithDataDto<AdminApiUserDto>> getAuthUser(
      Authentication authentication) {
    return AdminApiResponseUtils.toOkResponseEntity(
        new AdminApiUserDto(authentication.isAuthenticated(), authentication.getName()));
  }
}
