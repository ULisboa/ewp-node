package pt.ulisboa.ewp.node.api.admin.security;

import java.util.Collection;
import java.util.Objects;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import pt.ulisboa.ewp.node.api.common.security.jwt.JwtAuthenticationUserDetails;

public class AdminApiAuthenticationToken extends AbstractAuthenticationToken {

  private final transient JwtAuthenticationUserDetails details;

  public AdminApiAuthenticationToken(
      JwtAuthenticationUserDetails details, Collection<SimpleGrantedAuthority> grantedAuthorities) {
    super(grantedAuthorities);
    this.details = details;
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
  public Object getPrincipal() {
    return null;
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
    AdminApiAuthenticationToken that = (AdminApiAuthenticationToken) o;
    return Objects.equals(details, that.details);
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), details);
  }
}
