package pt.ulisboa.ewp.node.api.ewp.controller.iias.approvals.cnr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.iias.approval.cnr.v1.IiaApprovalCnrResponseV1;
import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approval.cnr.InterInstitutionalAgreementApprovalCnrV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.approvals.cnr.MockInterInstitutionalAgreementApprovalCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

class EwpApiInterInstitutionalAgreementApprovalCnrV1ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @Autowired
  private RegistryClient registryClient;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testIiaApprovalCnr_TwoAdmissibleHostProviders_BothHostProvidersInvoked(
      HttpMethod method) throws Exception {
    String approvingHeiId = "approving-hei-id";
    String ownerHeiId = "owner-hei-id";
    String iiaId = "i1";

    MockInterInstitutionalAgreementApprovalCnrV1HostProvider mockProvider1 = Mockito.spy(
        new MockInterInstitutionalAgreementApprovalCnrV1HostProvider());
    MockInterInstitutionalAgreementApprovalCnrV1HostProvider mockProvider2 = Mockito.spy(
        new MockInterInstitutionalAgreementApprovalCnrV1HostProvider());

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(InterInstitutionalAgreementApprovalCnrV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.APPROVING_HEI_ID, approvingHeiId);
    queryParams.param(EwpApiParamConstants.OWNER_HEI_ID, ownerHeiId);
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiInterInstitutionalAgreementApprovalCnrV1Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiaApprovalCnrResponseV1 response = XmlUtils.unmarshall(responseXml,
        IiaApprovalCnrResponseV1.class);

    assertThat(response).isNotNull();

    verify(mockProvider1, times(1)).onChangeNotification(approvingHeiId, ownerHeiId, iiaId);
    verify(mockProvider2, times(1)).onChangeNotification(approvingHeiId, ownerHeiId, iiaId);
  }
}