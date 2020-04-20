package pt.ulisboa.ewp.node.api.admin.controller;

import io.swagger.v3.oas.annotations.Operation;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import pt.ulisboa.ewp.node.api.admin.annotation.AdminApiWithResponseBodyWrapper;
import pt.ulisboa.ewp.node.api.admin.dto.AdminApiNewUserProfileDTO;
import pt.ulisboa.ewp.node.api.admin.dto.AdminApiUserProfileDTO;
import pt.ulisboa.ewp.node.api.admin.security.AdminApiSecurityCommonConstants;
import pt.ulisboa.ewp.node.api.admin.utils.AdminApiConstants;
import pt.ulisboa.ewp.node.api.common.dto.ApiOperationStatusDTO;
import pt.ulisboa.ewp.node.domain.entity.user.UserProfile;
import pt.ulisboa.ewp.node.domain.mapper.UserProfileMapper;
import pt.ulisboa.ewp.node.domain.repository.UserProfileRepository;

@RestController
@AdminApi
@RequestMapping(AdminApiConstants.API_BASE_URI + "users")
@Secured({AdminApiSecurityCommonConstants.ROLE_ADMIN_WITH_PREFIX})
@AdminApiWithResponseBodyWrapper
@Validated
public class AdminApiUserProfileController extends AbstractAdminApiController {

  @Autowired private UserProfileRepository userProfileRepository;

  @GetMapping
  @Operation(
      summary = "Get list of registered user profiles.",
      tags = {"admin"})
  public ResponseEntity<List<AdminApiUserProfileDTO>> getUserProfiles() {
    Collection<UserProfile> userProfiles = userProfileRepository.findAll();
    return ResponseEntity.ok(
        userProfiles.stream()
            .map(UserProfileMapper.INSTANCE::mapToDto)
            .collect(Collectors.toList()));
  }

  @PostMapping
  @Operation(
      summary = "Create user profile.",
      tags = {"admin"})
  public ResponseEntity<ApiOperationStatusDTO> create(
      @Valid @RequestBody AdminApiNewUserProfileDTO body) {
    boolean success =
        userProfileRepository.persist(UserProfile.create(body.getUsername(), body.getRole()));
    return getCreateEntityApiResponse(success);
  }
}
