package pt.ulisboa.ewp.node.config.manifest;

import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Scope;

@Scope(ConfigurableBeanFactory.SCOPE_SINGLETON)
@ConfigurationProperties(prefix = "manifest")
public class ManifestProperties {

  private ManifestEntriesProperties entries;

  public ManifestEntriesProperties getEntries() {
    return entries;
  }

  public void setEntries(ManifestEntriesProperties entries) {
    this.entries = entries;
  }

  public static ManifestProperties create(ManifestEntriesProperties entries) {
    ManifestProperties result = new ManifestProperties();
    result.setEntries(entries);
    return result;
  }
}
