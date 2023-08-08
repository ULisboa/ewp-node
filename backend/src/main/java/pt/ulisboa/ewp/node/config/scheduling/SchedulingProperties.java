package pt.ulisboa.ewp.node.config.scheduling;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties(prefix = "scheduling")
public class SchedulingProperties {

  private boolean enabled = false;

  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }
}
