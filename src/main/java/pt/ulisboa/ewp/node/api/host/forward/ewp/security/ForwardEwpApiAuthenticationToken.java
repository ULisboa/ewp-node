package pt.ulisboa.ewp.node.api.host.forward.ewp.security;

import java.util.Collections;
import java.util.Objects;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pt.ulisboa.ewp.node.api.host.forward.ewp.security.jwt.JwtAuthenticationUserDetails;

public class ForwardEwpApiAuthenticationToken extends AbstractAuthenticationToken {

  private final transient JwtAuthenticationUserDetails details;
  private final transient ForwardEwpApiHostPrincipal principal;

  public ForwardEwpApiAuthenticationToken(
      JwtAuthenticationUserDetails details, ForwardEwpApiHostPrincipal principal) {
    super(
        Collections.singletonList(
            new SimpleGrantedAuthority(
                ForwardEwpApiSecurityCommonConstants.ROLE_HOST_WITH_PREFIX)));
    this.details = details;
    this.principal = principal;
    setAuthenticated(true);
  }

  @Override
  public Object getCredentials() {
    return null;
  }

  @Override
  public JwtAuthenticationUserDetails getDetails() {
    return details;
  }

  @Override
  public ForwardEwpApiHostPrincipal getPrincipal() {
    return principal;
  }

  @Override
  public String getName() {
    return principal.toString();
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
    ForwardEwpApiAuthenticationToken that = (ForwardEwpApiAuthenticationToken) o;
    return Objects.equals(details, that.details) && Objects.equals(principal, that.principal);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), details, principal);
  }
}
