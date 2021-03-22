package pt.ulisboa.ewp.node.plugin.proxy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pt.ulisboa.ewp.host.plugin.skeleton.proxy.PluginPropertiesProxy;
import pt.ulisboa.ewp.node.utils.provider.ApplicationContextProvider;

public class DefaultPluginPropertiesProxy implements PluginPropertiesProxy {

  private static final Logger LOGGER = LoggerFactory.getLogger(DefaultPluginPropertiesProxy.class);

  private final String pluginId;

  public DefaultPluginPropertiesProxy(String pluginId) {
    this.pluginId = pluginId;
  }

  @Override
  public String getPropertyAsString(String key) {
    String completeKey = getCompletePropertyKey(key);
    try {
      return ApplicationContextProvider.getApplicationContext()
          .getEnvironment()
          .resolveRequiredPlaceholders("${" + completeKey + "}");
    } catch (IllegalArgumentException e) {
      LOGGER.debug("No property {} has been found", completeKey);
      return null;
    }
  }

  private String getCompletePropertyKey(String key) {
    return "plugins." + pluginId + "." + key;
  }
}
