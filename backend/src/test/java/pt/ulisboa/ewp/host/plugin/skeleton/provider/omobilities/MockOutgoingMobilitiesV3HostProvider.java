package pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities;

import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesUpdateRequestV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesUpdateResponseV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.StudentMobilityV3;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class MockOutgoingMobilitiesV3HostProvider extends OutgoingMobilitiesV3HostProvider {

  private final int maxOmobilityIdsPerRequest;

  private final Map<String, Collection<String>> heiIdToOmobilityIdsMap = new HashMap<>();
  private final Map<String, Collection<Pair<String, StudentMobilityV3>>> heiIdToOmobilitiesMap =
      new HashMap<>();

  public MockOutgoingMobilitiesV3HostProvider(int maxOmobilityIdsPerRequest) {
    this.maxOmobilityIdsPerRequest = maxOmobilityIdsPerRequest;
  }

  public MockOutgoingMobilitiesV3HostProvider registerOutgoingMobilityIds(
      String heiId, Collection<String> omobilityIds) {
    this.heiIdToOmobilityIdsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToOmobilityIdsMap.get(heiId).addAll(omobilityIds);
    return this;
  }

  public MockOutgoingMobilitiesV3HostProvider registerOutgoingMobility(
      String heiId, String outgoingMobilityId, StudentMobilityV3 mobility) {
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
  public Collection<String> findOutgoingMobilityIds(
      String requesterCoveredHeiId,
      String sendingHeiId,
      @Nullable String receivingAcademicYearId,
      @Nullable LocalDateTime modifiedSince,
      @Nullable String globalId,
      @Nullable String activityAttributes) {
    return heiIdToOmobilityIdsMap.get(sendingHeiId);
  }

  @Override
  public Collection<StudentMobilityV3> findBySendingHeiIdAndOutgoingMobilityIds(
      String requesterCoveredHeiId, String sendingHeiId, Collection<String> outgoingMobilityIds) {
    this.heiIdToOmobilitiesMap.computeIfAbsent(sendingHeiId, h -> new ArrayList<>());
    return heiIdToOmobilitiesMap.get(sendingHeiId).stream()
        .filter(e -> outgoingMobilityIds.contains(e.getLeft()))
        .map(Pair::getRight)
        .collect(Collectors.toList());
  }

  @Override
  public OmobilitiesUpdateResponseV3 updateOutgoingMobility(
      String requesterCoveredHeiId, String sendingHeiId, OmobilitiesUpdateRequestV3 updateData) {
    return new OmobilitiesUpdateResponseV3();
  }
}
