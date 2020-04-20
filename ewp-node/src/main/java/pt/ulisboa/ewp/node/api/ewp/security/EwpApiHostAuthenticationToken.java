package pt.ulisboa.ewp.node.api.ewp.security;

import java.util.Collections;
import java.util.Objects;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpApiHostAuthenticationToken extends AbstractAuthenticationToken {

  private final EwpAuthenticationMethod authenticationMethod;
  private final EwpApiHostPrincipal principal;

  public EwpApiHostAuthenticationToken(
      EwpAuthenticationMethod authenticationMethod, EwpApiHostPrincipal principal) {
    super(
        Collections.singletonList(
            new SimpleGrantedAuthority(EwpApiSecurityConstants.ROLE_HOST_WITH_PREFIX)));
    this.authenticationMethod = authenticationMethod;
    this.principal = principal;

    if (this.authenticationMethod != null
        && this.authenticationMethod != EwpAuthenticationMethod.ANONYMOUS) {
      setAuthenticated(true);
    }
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public EwpApiHostPrincipal getPrincipal() {
    return principal;
  }

  @Override
  public String getName() {
    return principal.getHeiIdsCoveredByClient().toString();
  }

  public EwpAuthenticationMethod getAuthenticationMethod() {
    return authenticationMethod;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    if (!super.equals(o)) {
      return false;
    }
    EwpApiHostAuthenticationToken that = (EwpApiHostAuthenticationToken) o;
    return authenticationMethod == that.authenticationMethod
        && Objects.equals(principal, that.principal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), authenticationMethod, principal);
  }
}
