package pt.ulisboa.ewp.node.config.bootstrap;

import java.util.List;

public class HostCoveredHeiBootstrapProperties {

  private String schacCode;
  private List<HeiLocalizedNameBootstrapProperties> names;
  private List<OtherHeiIdBootstrapProperties> otherHeiIds;

  public String getSchacCode() {
    return schacCode;
  }

  public void setSchacCode(String schacCode) {
    this.schacCode = schacCode;
  }

  public List<HeiLocalizedNameBootstrapProperties> getNames() {
    return names;
  }

  public void setNames(List<HeiLocalizedNameBootstrapProperties> names) {
    this.names = names;
  }

  public List<OtherHeiIdBootstrapProperties> getOtherHeiIds() {
    return otherHeiIds;
  }

  public void setOtherHeiIds(List<OtherHeiIdBootstrapProperties> otherHeiIds) {
    this.otherHeiIds = otherHeiIds;
  }
}
