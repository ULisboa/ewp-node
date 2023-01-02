package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server;

import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.httpsig.v1.SrvauthHttpsigV1;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EwpServerAuthenticationConfigurationFactory {

  private static EwpServerAuthenticationConfigurationFactory instance;

  private final Map<Class<?>, Function<Object, EwpServerAuthenticationConfiguration>> dictionary =
      new HashMap<>();

  public static EwpServerAuthenticationConfigurationFactory getInstance() {
    if (instance == null) {
      instance = new EwpServerAuthenticationConfigurationFactory();
    }
    return instance;
  }

  private EwpServerAuthenticationConfigurationFactory() {
    registerConverter(
        SrvauthHttpsigV1.class, o -> new EwpServerAuthenticationHttpSignatureConfiguration());
  }

  public <T> void registerConverter(
      Class<T> clazz, Function<T, EwpServerAuthenticationConfiguration> converter) {
    this.dictionary.put(clazz, object -> converter.apply(clazz.cast(object)));
  }

  public <T> EwpServerAuthenticationConfiguration create(T object) {
    if (dictionary.containsKey(object.getClass())) {
      return dictionary.get(object.getClass()).apply(object);
    } else {
      throw new IllegalArgumentException(
          "Unknown class type: " + object.getClass().getCanonicalName());
    }
  }
}
