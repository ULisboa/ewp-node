package pt.ulisboa.ewp.node.api.common.security.jwt;

import com.auth0.jwt.interfaces.DecodedJWT;

public class JwtAuthenticationUserDetails {

  private DecodedJWT token;

  public JwtAuthenticationUserDetails(DecodedJWT token) {
    this.token = token;
  }

  public DecodedJWT getToken() {
    return token;
  }
}
