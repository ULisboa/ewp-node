package pt.ulisboa.ewp.host.plugin.skeleton.provider.courses;

import eu.erasmuswithoutpaper.api.courses.v0.CoursesResponseV0.LearningOpportunitySpecification;
import jakarta.annotation.Nullable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;

public class MockCoursesV0HostProvider extends CoursesV0HostProvider {

  private final int maxLosIdsPerRequest;
  private final int maxLosCodesPerRequest;
  private final Map<String, Collection<Pair<String, LearningOpportunitySpecification>>> heiIdToLearningOpportunitiesByIdMap = new HashMap<>();
  private final Map<String, Collection<Pair<String, LearningOpportunitySpecification>>> heiIdToLearningOpportunitiesByCodeMap = new HashMap<>();

  public MockCoursesV0HostProvider(int maxLosIdsPerRequest, int maxLosCodesPerRequest) {
    this.maxLosIdsPerRequest = maxLosIdsPerRequest;
    this.maxLosCodesPerRequest = maxLosCodesPerRequest;
  }


  public MockCoursesV0HostProvider registerLearningOpportunitySpecification(String heiId,
      LearningOpportunitySpecification learningOpportunitySpecification) {
    this.heiIdToLearningOpportunitiesByIdMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToLearningOpportunitiesByIdMap.get(heiId).add(
        Pair.of(learningOpportunitySpecification.getLosId(), learningOpportunitySpecification));

    this.heiIdToLearningOpportunitiesByCodeMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToLearningOpportunitiesByCodeMap.get(heiId).add(
        Pair.of(learningOpportunitySpecification.getLosCode(), learningOpportunitySpecification));

    return this;
  }

  @Override
  public Collection<LearningOpportunitySpecification> findByHeiIdAndLosIds(String heiId,
      Collection<String> losIds, @Nullable LocalDate loisBefore, @Nullable LocalDate loisAfter,
      @Nullable LocalDate losAtDate) {
    return this.heiIdToLearningOpportunitiesByIdMap.get(heiId)
        .stream()
        .filter(e -> losIds.contains(e.getKey()))
        .map(Pair::getValue)
        .collect(Collectors.toList());
  }

  @Override
  public Collection<LearningOpportunitySpecification> findByHeiIdAndLosCodes(String heiId,
      Collection<String> losCodes, @Nullable LocalDate loisBefore, @Nullable LocalDate loisAfter,
      @Nullable LocalDate losAtDate) {
    return this.heiIdToLearningOpportunitiesByCodeMap.get(heiId)
        .stream()
        .filter(e -> losCodes.contains(e.getKey()))
        .map(Pair::getValue)
        .collect(Collectors.toList());
  }

  @Override
  public int getMaxLosIdsPerRequest() {
    return maxLosIdsPerRequest;
  }

  @Override
  public int getMaxLosCodesPerRequest() {
    return maxLosCodesPerRequest;
  }
}
