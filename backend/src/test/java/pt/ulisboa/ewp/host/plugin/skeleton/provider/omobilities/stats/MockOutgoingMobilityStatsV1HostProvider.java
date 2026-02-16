package pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.stats;

import eu.erasmuswithoutpaper.api.omobilities.stats.v1.OmobilityStatsResponseV1;
import java.util.HashMap;
import java.util.Map;

public class MockOutgoingMobilityStatsV1HostProvider extends
    OutgoingMobilityStatsV1HostProvider {

  private final Map<String, OmobilityStatsResponseV1> heiIdToStatsMap = new HashMap<>();

  public MockOutgoingMobilityStatsV1HostProvider registerStats(
      String heiId, OmobilityStatsResponseV1 statsResponse) {
    this.heiIdToStatsMap.put(heiId, statsResponse);
    return this;
  }

  @Override
  public OmobilityStatsResponseV1 getStats(String heiId) {
    return this.heiIdToStatsMap.get(heiId);
  }
}
