package pt.ulisboa.ewp.node.api.admin.dto.response.auth;

public class AdminApiUserDto {

  private final boolean isAuthenticated;
  private final String username;

  public AdminApiUserDto(boolean isAuthenticated, String username) {
    this.isAuthenticated = isAuthenticated;
    this.username = username;
  }

  public boolean isAuthenticated() {
    return isAuthenticated;
  }

  public String getUsername() {
    return username;
  }
}
