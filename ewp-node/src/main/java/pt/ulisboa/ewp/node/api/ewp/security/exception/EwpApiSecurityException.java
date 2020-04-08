package pt.ulisboa.ewp.node.api.ewp.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;

public class EwpApiSecurityException extends AuthenticationException {

  public enum AuthMethod {
    TLSCERT,
    HTTPSIG
  }

  private final HttpStatus status;
  private final AuthMethod authMethod;

  public EwpApiSecurityException(String message, HttpStatus status) {
    this(message, status, AuthMethod.TLSCERT);
  }

  public EwpApiSecurityException(String message, HttpStatus status, AuthMethod authMethod) {
    super(message);
    this.status = status;
    this.authMethod = authMethod;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public AuthMethod getAuthMethod() {
    return authMethod;
  }
}
