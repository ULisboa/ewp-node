package pt.ulisboa.ewp.node.api.ewp.controller.iias.approvals;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.iias.approval.v2.IiasApprovalResponseV2;
import eu.erasmuswithoutpaper.api.iias.approval.v2.IiasApprovalResponseV2.Approval;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.InterInstitutionalAgreementsApprovalV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

class EwpApiInterInstitutionalAgreementsApprovalV2ControllerTest
    extends AbstractEwpControllerIntegrationTest {

  @Autowired private HostPluginManager hostPluginManager;

  @Autowired private RegistryClient registryClient;

  @SpyBean private EwpInterInstitutionalAgreementMappingRepository mappingRepository;

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"GET", "POST"})
  public void
      testInterInstitutionalAgreementApprovalsRetrieval_ValidHeiIdDividedIntoTwoHostsWithExistingMappings_AllApprovalsReturned(
          HttpMethod method) throws Exception {
    String approvingHeiId = "test";
    String ownerHeiId = "owner-hei-id";
    List<String> iiaIds = Arrays.asList("a1", "b2", "c3");
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");

    List<Approval> approvals = new ArrayList<>();
    for (String iiaId : iiaIds) {
      Approval approval = new Approval();
      approval.setIiaId(iiaId);
      approval.setIiaHash(UUID.randomUUID().toString());
      approvals.add(approval);
    }

    MockInterInstitutionalAgreementsApprovalV2HostProvider mockProvider1 =
        new MockInterInstitutionalAgreementsApprovalV2HostProvider(3);
    mockProvider1.registerApprovals(approvingHeiId, approvals.get(0));

    MockInterInstitutionalAgreementsApprovalV2HostProvider mockProvider2 =
        new MockInterInstitutionalAgreementsApprovalV2HostProvider(3);
    mockProvider2.registerApprovals(approvingHeiId, approvals.get(1), approvals.get(2));

    for (int index = 0; index < iiaIds.size(); index++) {
      EwpInterInstitutionalAgreementMapping mapping =
          EwpInterInstitutionalAgreementMapping.create(
              approvingHeiId, ounitIds.get(index), iiaIds.get(index));
      doReturn(Optional.of(mapping))
          .when(mappingRepository)
          .findByHeiIdAndIiaId(approvingHeiId, iiaIds.get(index));
    }

    doReturn(true)
        .when(hostPluginManager)
        .hasHostProvider(approvingHeiId, InterInstitutionalAgreementsApprovalV2HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2))
        .when(hostPluginManager)
        .getAllProvidersOfType(
            approvingHeiId, InterInstitutionalAgreementsApprovalV2HostProvider.class);

    doReturn(Optional.of(mockProvider1))
        .when(hostPluginManager)
        .getSingleProvider(
            approvingHeiId,
            ounitIds.get(0),
            InterInstitutionalAgreementsApprovalV2HostProvider.class);

    doReturn(Optional.of(mockProvider2))
        .when(hostPluginManager)
        .getSingleProvider(
            approvingHeiId,
            ounitIds.get(1),
            InterInstitutionalAgreementsApprovalV2HostProvider.class);

    doReturn(Optional.of(mockProvider2))
        .when(hostPluginManager)
        .getSingleProvider(
            approvingHeiId,
            ounitIds.get(2),
            InterInstitutionalAgreementsApprovalV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.APPROVING_HEI_ID, approvingHeiId);
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiInterInstitutionalAgreementsApprovalV2Controller.BASE_PATH,
                queryParams,
                ownerHeiId)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasApprovalResponseV2 response =
        XmlUtils.unmarshall(responseXml, IiasApprovalResponseV2.class);

    assertThat(response).isNotNull();
    assertThat(response.getApproval()).hasSize(iiaIds.size());
    for (Approval approval : approvals) {
      Optional<Approval> approvalOptional =
          response.getApproval().stream()
              .filter(a -> a.getIiaId().equals(approval.getIiaId()))
              .findFirst();
      assertThat(approvalOptional).isPresent();
      assertThat(approvalOptional.get().getIiaHash()).isEqualTo(approval.getIiaHash());
    }
  }

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"GET", "POST"})
  public void
      testInterInstitutionalAgreementApprovalsRetrieval_ValidHeiIdDividedIntoTwoHostsWithNoMappingsButProviderHasAll_AllApprovalsReturned(
          HttpMethod method) throws Exception {
    String approvingHeiId = "test";
    String ownerHeiId = "owner-hei-id";
    List<String> iiaIds = Arrays.asList("a1", "b2", "c3");

    List<Approval> approvals = new ArrayList<>();
    for (String iiaId : iiaIds) {
      Approval approval = new Approval();
      approval.setIiaId(iiaId);
      approval.setIiaHash(UUID.randomUUID().toString());
      approvals.add(approval);
    }

    MockInterInstitutionalAgreementsApprovalV2HostProvider mockProvider1 =
        new MockInterInstitutionalAgreementsApprovalV2HostProvider(3);
    mockProvider1.registerApprovals(approvingHeiId, approvals.get(0));

    MockInterInstitutionalAgreementsApprovalV2HostProvider mockProvider2 =
        new MockInterInstitutionalAgreementsApprovalV2HostProvider(3);
    mockProvider2.registerApprovals(approvingHeiId, approvals.get(1), approvals.get(2));

    doReturn(true)
        .when(hostPluginManager)
        .hasHostProvider(approvingHeiId, InterInstitutionalAgreementsApprovalV2HostProvider.class);

    doReturn(Optional.of(mockProvider1))
        .when(hostPluginManager)
        .getPrimaryProvider(
            approvingHeiId, InterInstitutionalAgreementsApprovalV2HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2))
        .when(hostPluginManager)
        .getAllProvidersOfType(
            approvingHeiId, InterInstitutionalAgreementsApprovalV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.APPROVING_HEI_ID, approvingHeiId);
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiInterInstitutionalAgreementsApprovalV2Controller.BASE_PATH,
                queryParams,
                ownerHeiId)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasApprovalResponseV2 response =
        XmlUtils.unmarshall(responseXml, IiasApprovalResponseV2.class);

    assertThat(response).isNotNull();
    assertThat(response.getApproval()).hasSize(iiaIds.size());
    for (Approval approval : approvals) {
      Optional<Approval> approvalOptional =
          response.getApproval().stream()
              .filter(a -> a.getIiaId().equals(approval.getIiaId()))
              .findFirst();
      assertThat(approvalOptional).isPresent();
      assertThat(approvalOptional.get().getIiaHash()).isEqualTo(approval.getIiaHash());
    }
  }

  private static class MockInterInstitutionalAgreementsApprovalV2HostProvider
      extends InterInstitutionalAgreementsApprovalV2HostProvider {

    private final int maxIiaIdsPerRequest;

    private final Map<String, Collection<Pair<String, Approval>>> heiIdToApprovalsMap =
        new HashMap<>();

    MockInterInstitutionalAgreementsApprovalV2HostProvider(int maxIiaIdsPerRequest) {
      this.maxIiaIdsPerRequest = maxIiaIdsPerRequest;
    }

    public void registerApprovals(String heiId, Approval... approvals) {
      this.heiIdToApprovalsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
      Arrays.stream(approvals)
          .forEach(
              approval -> {
                this.heiIdToApprovalsMap.get(heiId).add(Pair.of(approval.getIiaId(), approval));
              });
    }

    @Override
    public int getMaxIiaIdsPerRequest() {
      return maxIiaIdsPerRequest;
    }

    @Override
    public Collection<Approval> findByIiaIds(
        String approvingHeiId, String requesterCoveredHeiId, Collection<String> iiaIds) {
      this.heiIdToApprovalsMap.computeIfAbsent(approvingHeiId, h -> new ArrayList<>());
      return heiIdToApprovalsMap.get(approvingHeiId).stream()
          .filter(e -> iiaIds.contains(e.getLeft()))
          .map(Pair::getRight)
          .collect(Collectors.toList());
    }
  }
}
