package pt.ulisboa.ewp.node.domain.entity.api.ewp.auth.server;

import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.httpsig.v1.SrvauthHttpsigV1;
import eu.erasmuswithoutpaper.api.client.auth.methods.srvauth.tlscert.v1.SrvauthTlscertV1;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EwpServerAuthenticationConfigurationFactory {

  private static final Logger LOG = LoggerFactory.getLogger(
      EwpServerAuthenticationConfigurationFactory.class);

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
    registerConverter(
        SrvauthTlscertV1.class, o -> new EwpServerAuthenticationTlsCertificateConfiguration());
  }

  public <T> void registerConverter(
      Class<T> clazz, Function<T, EwpServerAuthenticationConfiguration> converter) {
    this.dictionary.put(clazz, object -> converter.apply(clazz.cast(object)));
  }

  public <T> Optional<EwpServerAuthenticationConfiguration> create(T object) {
    if (dictionary.containsKey(object.getClass())) {
      return Optional.ofNullable(dictionary.get(object.getClass()).apply(object));
    } else {
      LOG.debug("Unknown class type: " + object.getClass().getCanonicalName());
      return Optional.empty();
    }
  }
}
