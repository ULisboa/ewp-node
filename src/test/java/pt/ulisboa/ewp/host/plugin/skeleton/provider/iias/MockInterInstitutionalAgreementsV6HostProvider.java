package pt.ulisboa.ewp.host.plugin.skeleton.provider.iias;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Triple;

public class MockInterInstitutionalAgreementsV6HostProvider extends
    InterInstitutionalAgreementsV6HostProvider {

  private final int maxIiaIdsPerRequest;
  private final int maxIiaCodesPerRequest;

  private final Map<String, Collection<String>> heiIdToIiaIdsMap = new HashMap<>();
  private final Map<String, Collection<Triple<String, String, Iia>>> heiIdToIiasMap = new HashMap<>();

  public MockInterInstitutionalAgreementsV6HostProvider(int maxIiaIdsPerRequest,
      int maxIiaCodesPerRequest) {
    this.maxIiaIdsPerRequest = maxIiaIdsPerRequest;
    this.maxIiaCodesPerRequest = maxIiaCodesPerRequest;
  }

  public MockInterInstitutionalAgreementsV6HostProvider registerIiaIds(String heiId,
      Collection<String> iiaIds) {
    this.heiIdToIiaIdsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToIiaIdsMap.get(heiId).addAll(iiaIds);
    return this;
  }

  public MockInterInstitutionalAgreementsV6HostProvider registerIia(String heiId, String iiaId,
      String iiaCode, Iia iia) {
    this.registerIiaIds(heiId, List.of(iiaId));
    this.heiIdToIiasMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    this.heiIdToIiasMap.get(heiId).add(Triple.of(iiaId, iiaCode, iia));
    return this;
  }

  @Override
  public int getMaxIiaIdsPerRequest() {
    return maxIiaIdsPerRequest;
  }

  @Override
  public int getMaxIiaCodesPerRequest() {
    return maxIiaCodesPerRequest;
  }

  @Override
  public Collection<String> findAllIiaIdsByHeiId(Collection<String> requesterCoveredHeiIds,
      String heiId, @Nullable String partnerHeiId,
      Collection<String> receivingAcademicYearIds, LocalDateTime modifiedSince) {
    return heiIdToIiaIdsMap.get(heiId);
  }

  @Override
  public Collection<Iia> findByHeiIdAndIiaIds(Collection<String> requesterCoveredHeiIds,
      String heiId, Collection<String> iiaIds,
      @Nullable Boolean sendPdf) {
    this.heiIdToIiasMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    return heiIdToIiasMap.get(heiId).stream()
        .filter(e -> iiaIds.contains(e.getLeft()))
        .map(Triple::getRight)
        .collect(Collectors.toList());
  }

  @Override
  public Collection<Iia> findByHeiIdAndIiaCodes(Collection<String> requesterCoveredHeiIds,
      String heiId, Collection<String> iiaCodes,
      @Nullable Boolean sendPdf) {
    this.heiIdToIiasMap.computeIfAbsent(heiId, h -> new ArrayList<>());
    return heiIdToIiasMap.get(heiId).stream()
        .filter(e -> iiaCodes.contains(e.getMiddle()))
        .map(Triple::getRight)
        .collect(Collectors.toList());
  }
}
