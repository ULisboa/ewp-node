package pt.ulisboa.ewp.node.api.host.forward.ewp.security;

import java.util.Collections;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import pt.ulisboa.ewp.node.api.common.security.jwt.JwtAuthenticationUserDetails;

public class ForwardEwpApiAuthenticationToken extends AbstractAuthenticationToken {

  private JwtAuthenticationUserDetails details;
  private ForwardEwpApiHostPrincipal principal;

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
}
