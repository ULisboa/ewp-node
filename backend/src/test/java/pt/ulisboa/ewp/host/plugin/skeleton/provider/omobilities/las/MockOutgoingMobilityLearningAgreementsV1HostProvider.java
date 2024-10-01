package pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las;

import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.LasOutgoingStatsResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.LearningAgreementV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateRequestV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateResponseV1;
import jakarta.annotation.Nullable;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import pt.ulisboa.ewp.host.plugin.skeleton.exceptions.EditConflictException;

public class MockOutgoingMobilityLearningAgreementsV1HostProvider extends
    OutgoingMobilityLearningAgreementsV1HostProvider {

  private final int maxOmobilityIdsPerRequest;

  private final Map<String, Collection<String>> heiIdToOmobilityIdsMap = new HashMap<>();
  private final Map<String, Collection<Pair<String, LearningAgreementV1>>> heiIdToLearningAgreementsMap = new HashMap<>();
  private final Map<String, OmobilityLasUpdateResponseV1> omobilityIdToUpdateResponseMap = new HashMap<>();
  private final Map<String, LasOutgoingStatsResponseV1> heiIdToStatsMap = new HashMap<>();

  public MockOutgoingMobilityLearningAgreementsV1HostProvider(int maxOmobilityIdsPerRequest) {
    this.maxOmobilityIdsPerRequest = maxOmobilityIdsPerRequest;
  }

  public MockOutgoingMobilityLearningAgreementsV1HostProvider registerOutgoingMobilityIds(
      String heiId,
      Collection<String> omobilityIds) {
    this.heiIdToOmobilityIdsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToOmobilityIdsMap.get(heiId).addAll(omobilityIds);
    return this;
  }

  public MockOutgoingMobilityLearningAgreementsV1HostProvider registerLearningAgreement(
      String heiId,
      String outgoingMobilityId, LearningAgreementV1 learningAgreement) {
    this.registerOutgoingMobilityIds(heiId, List.of(outgoingMobilityId));
    this.heiIdToLearningAgreementsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToLearningAgreementsMap.get(heiId)
        .add(Pair.of(outgoingMobilityId, learningAgreement));
    return this;
  }

  public MockOutgoingMobilityLearningAgreementsV1HostProvider registerUpdateDataToResponse(
      OmobilityLasUpdateRequestV1 updateData, OmobilityLasUpdateResponseV1 response) {
    if (updateData.getApproveProposalV1() != null) {
      this.omobilityIdToUpdateResponseMap.put(updateData.getApproveProposalV1().getOmobilityId(),
          response);
    } else {
      this.omobilityIdToUpdateResponseMap.put(updateData.getCommentProposalV1().getOmobilityId(),
          response);
    }
    return this;
  }

  public MockOutgoingMobilityLearningAgreementsV1HostProvider registerStats(
      String heiId, LasOutgoingStatsResponseV1 statsResponse) {
    this.heiIdToStatsMap.put(heiId, statsResponse);
    return this;
  }

  @Override
  public int getMaxOutgoingMobilityIdsPerRequest() {
    return maxOmobilityIdsPerRequest;
  }

  @Override
  public Collection<String> findOutgoingMobilityIds(Collection<String> requesterCoveredHeiIds,
      String sendingHeiId, Collection<String> receivingHeiIds,
      @Nullable String receivingAcademicYearId, @Nullable String globalId,
      @Nullable String mobilityType, @Nullable LocalDateTime modifiedSince) {
    return heiIdToOmobilityIdsMap.get(sendingHeiId);
  }

  @Override
  public Collection<LearningAgreementV1> findBySendingHeiIdAndOutgoingMobilityIds(
      Collection<String> requesterCoveredHeiIds,
      String sendingHeiId, Collection<String> outgoingMobilityIds) {
    this.heiIdToLearningAgreementsMap.computeIfAbsent(sendingHeiId, h -> new ArrayList<>());
    return heiIdToLearningAgreementsMap.get(sendingHeiId).stream()
        .filter(e -> outgoingMobilityIds.contains(e.getLeft()))
        .map(Pair::getRight)
        .collect(Collectors.toList());
  }

  @Override
  public OmobilityLasUpdateResponseV1 updateOutgoingMobilityLearningAgreement(
      Collection<String> requesterCoveredHeiIds, OmobilityLasUpdateRequestV1 updateData)
      throws EditConflictException {
    if (updateData.getApproveProposalV1() != null) {
      return this.omobilityIdToUpdateResponseMap.get(
          updateData.getApproveProposalV1().getOmobilityId());
    } else {
      return this.omobilityIdToUpdateResponseMap.get(
          updateData.getCommentProposalV1().getOmobilityId());
    }
  }

  @Override
  public LasOutgoingStatsResponseV1 getStats(String heiId) {
    return this.heiIdToStatsMap.get(heiId);
  }
}
