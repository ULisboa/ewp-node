package pt.ulisboa.ewp.node.api.ewp.controller.iias.approval;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalResponseV1;
import eu.erasmuswithoutpaper.api.iias.approval.v1.IiasApprovalResponseV1.Approval;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.InterInstitutionalAgreementsApprovalV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

class EwpApiInterInstitutionalAgreementsApprovalV1ControllerTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @Autowired
  private RegistryClient registryClient;

  @SpyBean
  private EwpInterInstitutionalAgreementMappingRepository mappingRepository;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testInterInstitutionalAgreementApprovalsRetrieval_ValidHeiIdDividedIntoTwoHosts_AllApprovalsReturned(
      HttpMethod method) throws Exception {
    String approvingHeiId = "test";
    List<String> iiaIds = Arrays.asList("a1", "b2", "c3");
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");

    List<Approval> approvals = new ArrayList<>();
    for (String iiaId : iiaIds) {
      Approval approval = new Approval();
      approval.setIiaId(iiaId);
      approval.setConditionsHash(UUID.randomUUID().toString());
      approvals.add(approval);
    }

    MockInterInstitutionalAgreementsApprovalV1HostProvider mockProvider1 = new MockInterInstitutionalAgreementsApprovalV1HostProvider(
        3);
    mockProvider1.registerApprovals(approvingHeiId, approvals.get(0));

    MockInterInstitutionalAgreementsApprovalV1HostProvider mockProvider2 = new MockInterInstitutionalAgreementsApprovalV1HostProvider(
        3);
    mockProvider2.registerApprovals(approvingHeiId, approvals.get(1), approvals.get(2));

    for (int index = 0; index < iiaIds.size(); index++) {
      EwpInterInstitutionalAgreementMapping mapping = EwpInterInstitutionalAgreementMapping.create(
          approvingHeiId, ounitIds.get(index),
          iiaIds.get(index), UUID.randomUUID().toString());
      doReturn(Optional.of(mapping)).when(mappingRepository)
          .findByHeiIdAndIiaId(approvingHeiId, iiaIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(approvingHeiId, InterInstitutionalAgreementsApprovalV1HostProvider.class);
    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(Mockito.anyString(),
            (Class<InterInstitutionalAgreementsApprovalV1HostProvider>) Mockito.any(Class.class));

    doReturn(Arrays.asList(mockProvider1)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(approvingHeiId, ounitIds.get(0),
            InterInstitutionalAgreementsApprovalV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(approvingHeiId, ounitIds.get(1),
            InterInstitutionalAgreementsApprovalV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(approvingHeiId, ounitIds.get(2),
            InterInstitutionalAgreementsApprovalV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.APPROVING_HEI_ID, approvingHeiId);
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiInterInstitutionalAgreementsApprovalV1Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasApprovalResponseV1 response = XmlUtils.unmarshall(responseXml,
        IiasApprovalResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getApproval()).hasSize(iiaIds.size());
    assertThat(response.getApproval().get(0).getIiaId()).isEqualTo(iiaIds.get(0));
    assertThat(response.getApproval().get(1).getIiaId()).isEqualTo(iiaIds.get(1));
    assertThat(response.getApproval().get(2).getIiaId()).isEqualTo(iiaIds.get(2));
    for (Approval approval : approvals) {
      Optional<Approval> approvalOptional = response.getApproval().stream()
          .filter(a -> a.getIiaId().equals(approval.getIiaId())).findFirst();
      assertThat(approvalOptional).isPresent();
      assertThat(approvalOptional.get().getConditionsHash()).isEqualTo(
          approval.getConditionsHash());
    }
  }

  private static class MockInterInstitutionalAgreementsApprovalV1HostProvider extends
      InterInstitutionalAgreementsApprovalV1HostProvider {

    private final int maxIiaIdsPerRequest;

    private final Map<String, Collection<Pair<String, Approval>>> heiIdToApprovalsMap = new HashMap<>();

    MockInterInstitutionalAgreementsApprovalV1HostProvider(int maxIiaIdsPerRequest) {
      this.maxIiaIdsPerRequest = maxIiaIdsPerRequest;
    }

    public void registerApprovals(String heiId, Approval... approvals) {
      this.heiIdToApprovalsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
      Arrays.stream(approvals).forEach(approval -> {
        this.heiIdToApprovalsMap.get(heiId).add(Pair.of(approval.getIiaId(), approval));
      });
    }

    @Override
    public int getMaxIiaIdsPerRequest() {
      return maxIiaIdsPerRequest;
    }

    @Override
    public Collection<Approval> findByIiaIds(String approvingHeiId, String ownerHeiId,
        Collection<String> iiaIds, @Nullable Boolean sendPdf) {
      this.heiIdToApprovalsMap.computeIfAbsent(approvingHeiId, h -> new ArrayList<>());
      return heiIdToApprovalsMap.get(approvingHeiId).stream()
          .filter(e -> iiaIds.contains(e.getLeft()))
          .map(Pair::getRight)
          .collect(Collectors.toList());
    }
  }

}