package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.cnr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.omobilities.cnr.v2.OmobilityCnrResponseV2;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.cnr.MockOutgoingMobilitiesCnrV2HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.cnr.OutgoingMobilityCnrV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.tests.provider.argument.HttpGetAndPostArgumentProvider;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

class EwpApiOutgoingMobilityCnrV2ControllerIntegrationTest
    extends AbstractEwpControllerIntegrationTest {

  @Autowired private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  void testOutgoingMobilityCnr_TwoAdmissibleHostProviders_BothHostProvidersInvoked(
      HttpMethod method) throws Exception {
    String sendingHeiId = "sending-hei-id";
    String receivingHeiId = "receiving-hei-id";
    String omobilityId = "om1";

    MockOutgoingMobilitiesCnrV2HostProvider mockProvider1 =
        Mockito.spy(new MockOutgoingMobilitiesCnrV2HostProvider());
    MockOutgoingMobilitiesCnrV2HostProvider mockProvider2 =
        Mockito.spy(new MockOutgoingMobilitiesCnrV2HostProvider());

    doReturn(Arrays.asList(mockProvider1, mockProvider2))
        .when(hostPluginManager)
        .getAllProvidersOfType(receivingHeiId, OutgoingMobilityCnrV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityId);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiOutgoingMobilityCnrV2Controller.BASE_PATH
                    + "/"
                    + receivingHeiId,
                queryParams,
                sendingHeiId)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilityCnrResponseV2 response =
        XmlUtils.unmarshall(responseXml, OmobilityCnrResponseV2.class);

    assertThat(response).isNotNull();

    verify(mockProvider1, times(1)).onChangeNotification(sendingHeiId, List.of(omobilityId));
    verify(mockProvider2, times(1)).onChangeNotification(sendingHeiId, List.of(omobilityId));
  }
}
