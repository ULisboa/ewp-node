package pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities;

import eu.erasmuswithoutpaper.api.imobilities.v1.endpoints.StudentMobilityForStudiesV1;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class MockIncomingMobilitiesV1HostProvider extends IncomingMobilitiesV1HostProvider {

  private final int maxOmobilityIdsPerRequest;

  private final Map<String, Collection<Pair<String, StudentMobilityForStudiesV1>>> heiIdToOmobilitiesMap = new HashMap<>();

  public MockIncomingMobilitiesV1HostProvider(int maxOmobilityIdsPerRequest) {
    this.maxOmobilityIdsPerRequest = maxOmobilityIdsPerRequest;
  }

  public MockIncomingMobilitiesV1HostProvider registerIncomingMobility(String heiId,
      String outgoingMobilityId, StudentMobilityForStudiesV1 mobility) {
    this.heiIdToOmobilitiesMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToOmobilitiesMap.get(heiId).add(Pair.of(outgoingMobilityId, mobility));
    return this;
  }

  @Override
  public int getMaxOutgoingMobilityIdsPerRequest() {
    return maxOmobilityIdsPerRequest;
  }

  @Override
  public Collection<eu.erasmuswithoutpaper.api.imobilities.v1.endpoints.StudentMobilityForStudiesV1> findByReceivingHeiIdAndOutgoingMobilityIds(
      Collection<String> requesterCoveredHeiIds, String receivingHeiId,
      Collection<String> outgoingMobilityIds) {
    this.heiIdToOmobilitiesMap.computeIfAbsent(receivingHeiId, h -> new ArrayList<>());
    return heiIdToOmobilitiesMap.get(receivingHeiId).stream()
        .filter(e -> outgoingMobilityIds.contains(e.getLeft()))
        .map(Pair::getRight)
        .collect(Collectors.toList());
  }
}
