package pt.ulisboa.ewp.node.config.manifest;

public class ManifestEntriesProperties {

  private boolean excludeIfNoPrimaryProviderAvailable;

  public boolean mustExcludeIfNoPrimaryProviderAvailable() {
    return excludeIfNoPrimaryProviderAvailable;
  }

  public void setExcludeIfNoPrimaryProviderAvailable(boolean excludeIfNoPrimaryProviderAvailable) {
    this.excludeIfNoPrimaryProviderAvailable = excludeIfNoPrimaryProviderAvailable;
  }

  public static ManifestEntriesProperties create(boolean excludeIfNoPrimaryProviderAvailable) {
    ManifestEntriesProperties result = new ManifestEntriesProperties();
    result.setExcludeIfNoPrimaryProviderAvailable(excludeIfNoPrimaryProviderAvailable);
    return result;
  }
}
