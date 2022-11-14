package pt.ulisboa.ewp.node.config.sync;

public class SyncMappingsProperties {

  private long intervalInMilliseconds;

  public long getIntervalInMilliseconds() {
    return intervalInMilliseconds;
  }

  public void setIntervalInMilliseconds(long intervalInMilliseconds) {
    this.intervalInMilliseconds = intervalInMilliseconds;
  }

  public static SyncMappingsProperties create(long intervalInMilliseconds) {
    SyncMappingsProperties result = new SyncMappingsProperties();
    result.setIntervalInMilliseconds(intervalInMilliseconds);
    return result;
  }
}
