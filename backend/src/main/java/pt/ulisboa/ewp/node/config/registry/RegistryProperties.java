package pt.ulisboa.ewp.node.config.registry;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties(prefix = "registry")
public class RegistryProperties {

  private String url;
  private boolean autoRefresh;
  private long timeBetweenRetriesInMilliseconds;
  private long minTimeBetweenQueriesInMilliseconds;
  private String validatorHeiIdsRegex;

  public String getUrl() {
    return url;
  }

  public void setUrl(String url) {
    this.url = url;
  }

  public boolean isAutoRefresh() {
    return autoRefresh;
  }

  public void setAutoRefresh(boolean autoRefresh) {
    this.autoRefresh = autoRefresh;
  }

  public long getTimeBetweenRetriesInMilliseconds() {
    return timeBetweenRetriesInMilliseconds;
  }

  public void setTimeBetweenRetriesInMilliseconds(long timeBetweenRetriesInMilliseconds) {
    this.timeBetweenRetriesInMilliseconds = timeBetweenRetriesInMilliseconds;
  }

  public long getMinTimeBetweenQueriesInMilliseconds() {
    return minTimeBetweenQueriesInMilliseconds;
  }

  public void setMinTimeBetweenQueriesInMilliseconds(long minTimeBetweenQueriesInMilliseconds) {
    this.minTimeBetweenQueriesInMilliseconds = minTimeBetweenQueriesInMilliseconds;
  }

  public String getValidatorHeiIdsRegex() {
    return validatorHeiIdsRegex;
  }

  public void setValidatorHeiIdsRegex(String validatorHeiIdsRegex) {
    this.validatorHeiIdsRegex = validatorHeiIdsRegex;
  }
}
