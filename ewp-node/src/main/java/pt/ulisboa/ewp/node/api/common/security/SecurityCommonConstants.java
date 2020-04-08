package pt.ulisboa.ewp.node.api.common.security;

public class SecurityCommonConstants {

  public static final String HEADER_NAME = "Authorization";
  public static final String BEATER_TOKEN_PREFIX = "Bearer ";

  public static final String ERROR_EXPIRED_TOKEN_CODE = "error.auth.expiredToken";
  public static final String ERROR_INVALID_TOKEN_CODE = "error.auth.invalidToken";
  public static final String ERROR_VERIFICATION_ERROR_CODE = "error.auth.verificationError";
  public static final String ERROR_UNKNOWN_USER_CODE = "error.auth.unknownUser";
}
