package pt.ulisboa.ewp.host.plugin.skeleton;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.pf4j.DefaultPluginManager;
import org.pf4j.PluginWrapper;

public class MockHostPlugin extends HostPlugin {

  private final Collection<String> coveredHeiIds;
  private final Map<String, Collection<String>> coveredOunitIdsByHeiId;
  private final Map<String, Collection<String>> coveredOunitCodesByHeiId;
  private final Collection<String> heiIdsOnWhichIsPrimary;

  MockHostPlugin(
      Collection<String> coveredHeiIds,
      Map<String, Collection<String>> coveredOunitIdsByHeiId,
      Map<String, Collection<String>> coveredOunitCodesByHeiId,
      Collection<String> heiIdsOnWhichIsPrimary) {
    super(new PluginWrapper(new DefaultPluginManager(), null, null, null));
    this.coveredHeiIds = coveredHeiIds;
    this.coveredOunitIdsByHeiId = coveredOunitIdsByHeiId;
    this.coveredOunitCodesByHeiId = coveredOunitCodesByHeiId;
    this.heiIdsOnWhichIsPrimary = heiIdsOnWhichIsPrimary;
  }

  @Override
  public Collection<String> getCoveredHeiIds() {
    return coveredHeiIds;
  }

  @Override
  public Collection<String> getCoveredOunitIdsByHeiId(String heiId) {
    return coveredOunitIdsByHeiId.getOrDefault(heiId, new ArrayList<>());
  }

  @Override
  public Collection<String> getCoveredOunitCodesByHeiId(String heiId) {
    return coveredOunitCodesByHeiId.getOrDefault(heiId, new ArrayList<>());
  }

  @Override
  public boolean isPrimaryForHeiId(String heiId) {
    return heiIdsOnWhichIsPrimary.contains(heiId);
  }

  public static class Builder {

    private Collection<String> coveredHeiIds = new ArrayList<>();
    private Map<String, Collection<String>> coveredOunitIdsByHeiId = new HashMap<>();
    private Map<String, Collection<String>> coveredOunitCodesByHeiId = new HashMap<>();
    private Collection<String> heiIdsOnWhichIsPrimary = new ArrayList<>();

    public Builder coveredHeiId(String coveredHeiId) {
      this.coveredHeiIds.add(coveredHeiId);
      return this;
    }

    public Builder coveredOunitIdsByHeiId(String heiId, Collection<String> coveredOunitIdsByHeiId) {
      this.coveredOunitIdsByHeiId.computeIfAbsent(heiId, h -> new ArrayList<>());
      this.coveredOunitIdsByHeiId.get(heiId).addAll(coveredOunitIdsByHeiId);
      return this;
    }

    public Builder coveredOunitCodesByHeiId(
        String heiId, Collection<String> coveredOunitCodesByHeiId) {
      this.coveredOunitCodesByHeiId.computeIfAbsent(heiId, h -> new ArrayList<>());
      this.coveredOunitCodesByHeiId.get(heiId).addAll(coveredOunitCodesByHeiId);
      return this;
    }

    public Builder heiIdOnWhichIsPrimary(String heiIdOnWhichIsPrimary) {
      this.heiIdsOnWhichIsPrimary.add(heiIdOnWhichIsPrimary);
      return this;
    }

    public MockHostPlugin build() {
      return new MockHostPlugin(
          coveredHeiIds, coveredOunitIdsByHeiId, coveredOunitCodesByHeiId, heiIdsOnWhichIsPrimary);
    }
  }
}
