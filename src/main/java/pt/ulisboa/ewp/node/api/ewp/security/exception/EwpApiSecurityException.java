package pt.ulisboa.ewp.node.api.ewp.security.exception;

import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpApiSecurityException extends AuthenticationException {

  private final HttpStatus status;
  private final EwpAuthenticationMethod authMethod;

  public EwpApiSecurityException(String message, HttpStatus status,
      EwpAuthenticationMethod authMethod) {
    super(message);
    this.status = status;
    this.authMethod = authMethod;
  }

  public HttpStatus getStatus() {
    return status;
  }

  public EwpAuthenticationMethod getAuthMethod() {
    return authMethod;
  }
}
