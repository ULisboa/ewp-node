package pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.tors;

import eu.erasmuswithoutpaper.api.imobilities.tors.v2.endpoints.ImobilityTorsGetResponseV2.Tor;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;

public class MockIncomingMobilityToRsV2HostProvider extends
    IncomingMobilityToRsV2HostProvider {

  private final int maxOmobilityIdsPerRequest;

  private final Map<String, Collection<String>> heiIdToOmobilityIdsMap = new HashMap<>();
  private final Map<String, Collection<Pair<String, Tor>>> heiIdToToRsMap = new HashMap<>();

  public MockIncomingMobilityToRsV2HostProvider(int maxOmobilityIdsPerRequest) {
    this.maxOmobilityIdsPerRequest = maxOmobilityIdsPerRequest;
  }

  public MockIncomingMobilityToRsV2HostProvider registerOutgoingMobilityIds(
      String heiId,
      Collection<String> omobilityIds) {
    this.heiIdToOmobilityIdsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToOmobilityIdsMap.get(heiId).addAll(omobilityIds);
    return this;
  }

  public MockIncomingMobilityToRsV2HostProvider registerTranscriptOfRecords(
      String heiId, String outgoingMobilityId, Tor transcriptOfRecord) {
    this.registerOutgoingMobilityIds(heiId, List.of(outgoingMobilityId));
    this.heiIdToToRsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToToRsMap.get(heiId).add(Pair.of(outgoingMobilityId, transcriptOfRecord));
    return this;
  }

  @Override
  public int getMaxOutgoingMobilityIdsPerRequest() {
    return maxOmobilityIdsPerRequest;
  }

  @Override
  public Collection<String> findOutgoingMobilityIds(Collection<String> requesterCoveredHeiIds,
      String receivingHeiId, Collection<String> sendingHeiIds,
      @Nullable LocalDateTime modifiedSince) {
    return heiIdToOmobilityIdsMap.get(receivingHeiId);
  }

  @Override
  public Collection<Tor> findByReceivingHeiIdAndOutgoingMobilityIds(
      Collection<String> requesterCoveredHeiIds, String receivingHeiId,
      Collection<String> outgoingMobilityIds) {
    this.heiIdToToRsMap.computeIfAbsent(receivingHeiId, h -> new ArrayList<>());
    return heiIdToToRsMap.get(receivingHeiId).stream()
        .filter(e -> outgoingMobilityIds.contains(e.getLeft()))
        .map(Pair::getRight)
        .collect(Collectors.toList());
  }
}
