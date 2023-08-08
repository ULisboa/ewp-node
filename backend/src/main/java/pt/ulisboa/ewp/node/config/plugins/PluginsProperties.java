package pt.ulisboa.ewp.node.config.plugins;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties(prefix = "plugins")
public class PluginsProperties {

  private String path;
  private PluginsAspectsProperties aspects;

  public String getPath() {
    return path;
  }

  public void setPath(String path) {
    this.path = path;
  }

  public PluginsAspectsProperties getAspects() {
    return aspects;
  }

  public void setAspects(PluginsAspectsProperties aspects) {
    this.aspects = aspects;
  }
}
