package pt.ulisboa.ewp.node.config.cnr;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties(prefix = "cnr")
public class CnrProperties {

  private long intervalInMilliseconds;
  private int maxNumberAttempts;

  public long getIntervalInMilliseconds() {
    return intervalInMilliseconds;
  }

  public void setIntervalInMilliseconds(long intervalInMilliseconds) {
    this.intervalInMilliseconds = intervalInMilliseconds;
  }

  public int getMaxNumberAttempts() {
    return maxNumberAttempts;
  }

  public void setMaxNumberAttempts(int maxNumberAttempts) {
    this.maxNumberAttempts = maxNumberAttempts;
  }
}
