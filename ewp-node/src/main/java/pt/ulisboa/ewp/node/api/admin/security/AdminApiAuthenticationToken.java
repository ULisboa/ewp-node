package pt.ulisboa.ewp.node.api.admin.security;

import java.util.Collection;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import pt.ulisboa.ewp.node.api.common.security.jwt.JwtAuthenticationUserDetails;

public class AdminApiAuthenticationToken extends AbstractAuthenticationToken {

  private JwtAuthenticationUserDetails details;

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
}
