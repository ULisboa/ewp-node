package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.httpsig.v1.CliauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.none.v1.CliauthAnonymousV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.tlscert.v1.CliauthTlscertV1;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class EwpClientAuthenticationConfigurationFactory {

  private static EwpClientAuthenticationConfigurationFactory instance;

  private final Map<Class<?>, Function<Object, EwpClientAuthenticationConfiguration>> dictionary =
      new HashMap<>();

  public static EwpClientAuthenticationConfigurationFactory getInstance() {
    if (instance == null) {
      instance = new EwpClientAuthenticationConfigurationFactory();
    }
    return instance;
  }

  private EwpClientAuthenticationConfigurationFactory() {
    registerConverter(
        CliauthTlscertV1.class,
        o -> new EwpClientAuthenticationTlsCertificateConfiguration(o.isAllowsSelfSigned()));

    registerConverter(
        CliauthHttpsigV1.class, o -> new EwpClientAuthenticationHttpSignatureConfiguration());

    registerConverter(
        CliauthAnonymousV1.class, o -> new EwpClientAuthenticationAnonymousConfiguration());
  }

  public <T> void registerConverter(
      Class<T> clazz, Function<T, EwpClientAuthenticationConfiguration> converter) {
    this.dictionary.put(clazz, object -> converter.apply(clazz.cast(object)));
  }

  public <T> EwpClientAuthenticationConfiguration create(T object) {
    if (dictionary.containsKey(object.getClass())) {
      return dictionary.get(object.getClass()).apply(object);
    } else {
      throw new IllegalArgumentException(
          "Unknown class type: " + object.getClass().getCanonicalName());
    }
  }
}
