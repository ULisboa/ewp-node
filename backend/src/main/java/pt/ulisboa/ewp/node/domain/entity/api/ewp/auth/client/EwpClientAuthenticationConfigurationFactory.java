package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.client;

import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.httpsig.v1.CliauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.cliauth.none.v1.CliauthAnonymousV1;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EwpClientAuthenticationConfigurationFactory {

  private static final Logger LOG = LoggerFactory.getLogger(
      EwpClientAuthenticationConfigurationFactory.class);

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
        CliauthHttpsigV1.class, o -> new EwpClientAuthenticationHttpSignatureConfiguration());

    registerConverter(
        CliauthAnonymousV1.class, o -> new EwpClientAuthenticationAnonymousConfiguration());
  }

  public <T> void registerConverter(
      Class<T> clazz, Function<T, EwpClientAuthenticationConfiguration> converter) {
    this.dictionary.put(clazz, object -> converter.apply(clazz.cast(object)));
  }

  public <T> Optional<EwpClientAuthenticationConfiguration> create(T object) {
    if (dictionary.containsKey(object.getClass())) {
      return Optional.ofNullable(dictionary.get(object.getClass()).apply(object));
    } else {
      LOG.debug("Unknown class type: " + object.getClass().getCanonicalName());
      return Optional.empty();
    }
  }
}
