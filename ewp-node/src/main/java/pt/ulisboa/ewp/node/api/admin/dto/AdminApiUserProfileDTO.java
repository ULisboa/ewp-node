package pt.ulisboa.ewp.node.api.admin.dto;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import pt.ulisboa.ewp.node.domain.entity.user.UserRole;

public class AdminApiUserProfileDTO {

  @NotNull @NotEmpty private String username;

  @NotNull private UserRole role;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public UserRole getRole() {
    return role;
  }

  public void setRole(UserRole role) {
    this.role = role;
  }
}
