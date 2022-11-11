package pt.ulisboa.ewp.node.api.host.forward.ewp.security;

public class ForwardEwpApiSecurityCommonConstants {

  private ForwardEwpApiSecurityCommonConstants() {
  }

  public static final String HEADER_NAME = "Authorization";
  public static final String BEATER_TOKEN_PREFIX = "Bearer ";

  public static final String ROLE_PREFIX = "ROLE_";
  public static final String ROLE_HOST = "HOST";
  public static final String ROLE_HOST_WITH_PREFIX = ROLE_PREFIX + ROLE_HOST;

  public static final String ERROR_VERIFICATION_ERROR_CODE = "error.auth.verificationError";
}
