package pt.ulisboa.ewp.node.api.ewp.controller.iias.cnr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.iias.cnr.v2.IiaCnrResponseV2;
import java.util.Arrays;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.cnr.InterInstitutionalAgreementCnrV2HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.cnr.MockInterInstitutionalAgreementsCnrV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

class EwpApiInterInstitutionalAgreementsCnrV2ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @Autowired
  private RegistryClient registryClient;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testIiaCnr_TwoAdmissibleHostProviders_BothHostProvidersInvoked(
      HttpMethod method) throws Exception {
    String notifierHeiId = "test";
    String iiaId = "i1";

    MockInterInstitutionalAgreementsCnrV2HostProvider mockProvider1 = Mockito.spy(
        new MockInterInstitutionalAgreementsCnrV2HostProvider());
    MockInterInstitutionalAgreementsCnrV2HostProvider mockProvider2 = Mockito.spy(
        new MockInterInstitutionalAgreementsCnrV2HostProvider());

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(InterInstitutionalAgreementCnrV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.NOTIFIER_HEI_ID, notifierHeiId);
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiInterInstitutionalAgreementsCnrV2Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiaCnrResponseV2 response = XmlUtils.unmarshall(responseXml, IiaCnrResponseV2.class);

    assertThat(response).isNotNull();

    verify(mockProvider1, times(1)).onChangeNotification(notifierHeiId, iiaId);
    verify(mockProvider2, times(1)).onChangeNotification(notifierHeiId, iiaId);
  }

}