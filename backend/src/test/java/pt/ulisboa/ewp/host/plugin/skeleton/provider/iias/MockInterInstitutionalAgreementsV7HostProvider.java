package pt.ulisboa.ewp.host.plugin.skeleton.provider.iias;

import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasStatsResponseV7;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Triple;

public class MockInterInstitutionalAgreementsV7HostProvider extends
    InterInstitutionalAgreementsV7HostProvider {

  private final int maxIiaIdsPerRequest;

  private final Map<String, Collection<String>> heiIdToIiaIdsMap = new HashMap<>();
  private final Map<String, Collection<Triple<String, String, Iia>>> heiIdToIiasMap = new HashMap<>();
  private final Map<String, IiasStatsResponseV7> heiIdToStatsMap = new HashMap<>();

  public MockInterInstitutionalAgreementsV7HostProvider(int maxIiaIdsPerRequest,
      int maxIiaCodesPerRequest) {
    this.maxIiaIdsPerRequest = maxIiaIdsPerRequest;
  }

  public MockInterInstitutionalAgreementsV7HostProvider registerIiaIds(String heiId,
      Collection<String> iiaIds) {
    this.heiIdToIiaIdsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToIiaIdsMap.get(heiId).addAll(iiaIds);
    return this;
  }

  public MockInterInstitutionalAgreementsV7HostProvider registerIia(String heiId, String iiaId,
      String iiaCode, Iia iia) {
    this.registerIiaIds(heiId, List.of(iiaId));
    this.heiIdToIiasMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToIiasMap.get(heiId).add(Triple.of(iiaId, iiaCode, iia));
    return this;
  }

  public MockInterInstitutionalAgreementsV7HostProvider registerStats(
      String heiId, IiasStatsResponseV7 statsResponse) {
    this.heiIdToStatsMap.put(heiId, statsResponse);
    return this;
  }

  @Override
  public int getMaxIiaIdsPerRequest() {
    return maxIiaIdsPerRequest;
  }

  @Override
  public Collection<String> findAllIiaIdsByHeiId(String requesterCoveredHeiId,
      String heiId, Collection<String> receivingAcademicYearIds, LocalDateTime modifiedSince) {
    return heiIdToIiaIdsMap.get(heiId);
  }

  @Override
  public Collection<Iia> findByHeiIdAndIiaIds(String requesterCoveredHeiId,
      String heiId, Collection<String> iiaIds) {
    this.heiIdToIiasMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    return heiIdToIiasMap.get(heiId).stream()
        .filter(e -> iiaIds.contains(e.getLeft()))
        .map(Triple::getRight)
        .collect(Collectors.toList());
  }

  @Override
  public IiasStatsResponseV7 getStats(String heiId) {
    return this.heiIdToStatsMap.get(heiId);
  }
}
