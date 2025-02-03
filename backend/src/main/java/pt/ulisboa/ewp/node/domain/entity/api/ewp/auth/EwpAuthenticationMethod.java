package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth;

import java.util.List;

public enum EwpAuthenticationMethod {
  HTTP_SIGNATURE,
  TLS_CERTIFICATE,
  ANONYMOUS;

  public static final List<EwpAuthenticationMethod>
      CLIENT_AUTHENTICATION_METHODS_BY_PREFERENTIAL_ORDER =
          List.of(EwpAuthenticationMethod.HTTP_SIGNATURE, EwpAuthenticationMethod.ANONYMOUS);

  public static final List<EwpAuthenticationMethod>
      SERVER_AUTHENTICATION_METHODS_BY_PREFERENTIAL_ORDER =
          List.of(
              EwpAuthenticationMethod.HTTP_SIGNATURE,
              EwpAuthenticationMethod.TLS_CERTIFICATE,
              EwpAuthenticationMethod.ANONYMOUS);
}
