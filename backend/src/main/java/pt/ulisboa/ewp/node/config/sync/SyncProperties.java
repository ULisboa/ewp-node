package pt.ulisboa.ewp.node.config.sync;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties(prefix = "sync")
public class SyncProperties {

  private SyncMappingsProperties mappings;

  public SyncMappingsProperties getMappings() {
    return mappings;
  }

  public void setMappings(
      SyncMappingsProperties mappings) {
    this.mappings = mappings;
  }

  public static SyncProperties create(SyncMappingsProperties syncMappingsProperties) {
    SyncProperties result = new SyncProperties();
    result.setMappings(syncMappingsProperties);
    return result;
  }
}
