package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth;

import java.util.List;

public enum EwpAuthenticationMethod {
  HTTP_SIGNATURE,
  ANONYMOUS;

  public static final List<EwpAuthenticationMethod> AUTHENTICATION_METHODS_BY_PREFERENTIAL_ORDER =
      List.of(EwpAuthenticationMethod.HTTP_SIGNATURE, EwpAuthenticationMethod.ANONYMOUS);
}
