package pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities;

import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.StudentMobilityV2;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class MockOutgoingMobilitiesV2HostProvider extends OutgoingMobilitiesV2HostProvider {

  private final int maxOmobilityIdsPerRequest;

  private final Map<String, Collection<String>> heiIdToOmobilityIdsMap = new HashMap<>();
  private final Map<String, Collection<Pair<String, StudentMobilityV2>>> heiIdToOmobilitiesMap = new HashMap<>();

  public MockOutgoingMobilitiesV2HostProvider(int maxOmobilityIdsPerRequest) {
    this.maxOmobilityIdsPerRequest = maxOmobilityIdsPerRequest;
  }

  public MockOutgoingMobilitiesV2HostProvider registerOutgoingMobilityIds(String heiId,
      Collection<String> omobilityIds) {
    this.heiIdToOmobilityIdsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToOmobilityIdsMap.get(heiId).addAll(omobilityIds);
    return this;
  }

  public MockOutgoingMobilitiesV2HostProvider registerOutgoingMobility(String heiId,
      String outgoingMobilityId,
      StudentMobilityV2 mobility) {
    this.registerOutgoingMobilityIds(heiId, List.of(outgoingMobilityId));
    this.heiIdToOmobilitiesMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToOmobilitiesMap.get(heiId).add(Pair.of(outgoingMobilityId, mobility));
    return this;
  }

  @Override
  public int getMaxOutgoingMobilityIdsPerRequest() {
    return maxOmobilityIdsPerRequest;
  }

  @Override
  public Collection<String> findOutgoingMobilityIds(Collection<String> requesterCoveredHeiIds,
      String sendingHeiId,
      Collection<String> receivingHeiIds, @Nullable String receivingAcademicYearId,
      @Nullable LocalDateTime modifiedSince) {
    return heiIdToOmobilityIdsMap.get(sendingHeiId);
  }

  @Override
  public Collection<StudentMobilityV2> findBySendingHeiIdAndOutgoingMobilityIds(
      Collection<String> requesterCoveredHeiIds,
      String sendingHeiId, Collection<String> outgoingMobilityIds) {
    this.heiIdToOmobilitiesMap.computeIfAbsent(sendingHeiId, h -> new ArrayList<>());
    return heiIdToOmobilitiesMap.get(sendingHeiId).stream()
        .filter(e -> outgoingMobilityIds.contains(e.getLeft()))
        .map(Pair::getRight)
        .collect(Collectors.toList());
  }
}
