package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.cnr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.omobilities.cnr.v1.OmobilityCnrResponseV1;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.cnr.MockOutgoingMobilitiesCnrV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.cnr.OutgoingMobilityCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

class EwpApiOutgoingMobilityCnrV1ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  void testOutgoingMobilityCnr_TwoAdmissibleHostProviders_BothHostProvidersInvoked(
      HttpMethod method) throws Exception {
    String sendingHeiId = "test";
    String omobilityId = "om1";

    MockOutgoingMobilitiesCnrV1HostProvider mockProvider1 = Mockito.spy(
        new MockOutgoingMobilitiesCnrV1HostProvider());
    MockOutgoingMobilitiesCnrV1HostProvider mockProvider2 = Mockito.spy(
        new MockOutgoingMobilitiesCnrV1HostProvider());

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(OutgoingMobilityCnrV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiOutgoingMobilityCnrV1Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilityCnrResponseV1 response = XmlUtils.unmarshall(responseXml,
        OmobilityCnrResponseV1.class);

    assertThat(response).isNotNull();

    verify(mockProvider1, times(1)).onChangeNotification(sendingHeiId, List.of(omobilityId));
    verify(mockProvider2, times(1)).onChangeNotification(sendingHeiId, List.of(omobilityId));
  }
}