package pt.ulisboa.ewp.node.api.ewp.controller.imobilities.cnr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.imobilities.cnr.v1.ImobilityCnrResponseV1;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.cnr.IncomingMobilityCnrV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.cnr.MockIncomingMobilitiesCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

class EwpApiIncomingMobilityCnrV1ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  void testIncomingMobilityCnr_TwoAdmissibleHostProviders_BothHostProvidersInvoked(
      HttpMethod method) throws Exception {
    String receivingHeiId = "test";
    String omobilityId = "om1";

    MockIncomingMobilitiesCnrV1HostProvider mockProvider1 = Mockito.spy(
        new MockIncomingMobilitiesCnrV1HostProvider());
    MockIncomingMobilitiesCnrV1HostProvider mockProvider2 = Mockito.spy(
        new MockIncomingMobilitiesCnrV1HostProvider());

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(IncomingMobilityCnrV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiIncomingMobilityCnrV1Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    ImobilityCnrResponseV1 response = XmlUtils.unmarshall(responseXml,
        ImobilityCnrResponseV1.class);

    assertThat(response).isNotNull();

    verify(mockProvider1, times(1)).onChangeNotification(receivingHeiId, List.of(omobilityId));
    verify(mockProvider2, times(1)).onChangeNotification(receivingHeiId, List.of(omobilityId));
  }
}