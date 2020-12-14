package pt.ulisboa.ewp.node.client.ewp.utils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.EwpAuthenticationMethod;

public class EwpClientConstants {

  private EwpClientConstants() {}

  public static final List<EwpAuthenticationMethod> AUTHENTICATION_METHODS_BY_PREFERENTIAL_ORDER =
      Collections.unmodifiableList(
          Arrays.asList(
              EwpAuthenticationMethod.HTTP_SIGNATURE,
              EwpAuthenticationMethod.TLS,
              EwpAuthenticationMethod.ANONYMOUS));
}
