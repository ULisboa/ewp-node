package pt.ulisboa.ewp.node.plugin.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.env.Environment;
import pt.ulisboa.ewp.host.plugin.skeleton.proxy.PluginPropertiesProxy;

public class DefaultPluginPropertiesProxy implements PluginPropertiesProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPluginPropertiesProxy.class);

  private final String pluginId;
  private final Environment environment;

  public DefaultPluginPropertiesProxy(String pluginId, Environment environment) {
    this.pluginId = pluginId;
    this.environment = environment;
  }

  @Override
  public String getPropertyAsString(String key) {
    if (environment == null) {
      LOGGER.debug("Environment is not available");
      return null;
    }

    String completeKey = getCompletePropertyKey(key);
    try {
      return environment.resolveRequiredPlaceholders("${" + completeKey + "}");
    } catch (IllegalArgumentException e) {
      LOGGER.debug("No property {} has been found", completeKey);
      return null;
    }
  }

  private String getCompletePropertyKey(String key) {
    return "plugins." + pluginId + "." + key;
  }
}
