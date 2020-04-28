package pt.ulisboa.ewp.node.api.admin.security;

public class AdminApiSecurityCommonConstants {

  private AdminApiSecurityCommonConstants() {}

  public static final String HEADER_NAME = "Authorization";
  public static final String TOKEN_PREFIX = "Bearer ";

  public static final String ROLE_PREFIX = "ROLE_";
  public static final String ROLE_ADMIN = "ADMIN";
  public static final String ROLE_ADMIN_WITH_PREFIX = ROLE_PREFIX + ROLE_ADMIN;

  public static final String USER_ADMIN = "admin";
}
