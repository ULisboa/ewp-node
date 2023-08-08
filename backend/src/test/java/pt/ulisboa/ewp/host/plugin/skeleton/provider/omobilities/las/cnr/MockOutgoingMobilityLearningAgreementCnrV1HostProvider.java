package pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.cnr;

import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.LasIncomingStatsResponseV1;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class MockOutgoingMobilityLearningAgreementCnrV1HostProvider extends
    OutgoingMobilityLearningAgreementCnrV1HostProvider {

  private final Map<String, LasIncomingStatsResponseV1> heiIdToStatsMap = new HashMap<>();

  public MockOutgoingMobilityLearningAgreementCnrV1HostProvider registerStats(
      String heiId, LasIncomingStatsResponseV1 statsResponse) {
    this.heiIdToStatsMap.put(heiId, statsResponse);
    return this;
  }

  @Override
  public void onChangeNotification(String sendingHeiId, Collection<String> outgoingMobilityIds) {

  }

  @Override
  public LasIncomingStatsResponseV1 getStats(String heiId) {
    return this.heiIdToStatsMap.get(heiId);
  }
}
