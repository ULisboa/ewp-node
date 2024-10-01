package pt.ulisboa.ewp.node.api.ewp.controller.courses.replication;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.courses.replication.v1.CourseReplicationResponseV1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.replication.SimpleCourseReplicationV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.tests.provider.argument.HttpGetAndPostArgumentProvider;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

class EwpApiSimpleCourseReplicationV1ControllerTest extends AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  public void testSimpleCourseReplicationRetrieval_UnknownHeiId_ErrorReturned(HttpMethod method)
      throws Exception {
    String heiId = "test";
    List<String> losIds = Arrays.asList("a1", "b2", "c3");

    doReturn(false).when(hostPluginManager)
        .hasHostProvider(heiId, SimpleCourseReplicationV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);

    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI + EwpApiSimpleCourseReplicationV1Controller.BASE_PATH,
        queryParams,
        "Unknown HEI ID: " + heiId);
  }

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  public void
      testSimpleCourseReplicationRetrieval_ValidHeiIdAndThreeValidOunitCodesDividedIntoTwoHosts_AllLosIdsReturned(
          HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> losIds = Arrays.asList("a1", "b2", "c3");

    MockSimpleCourseReplicationV1HostProvider mockProvider1 = new MockSimpleCourseReplicationV1HostProvider();
    MockSimpleCourseReplicationV1HostProvider mockProvider2 = new MockSimpleCourseReplicationV1HostProvider();

    mockProvider1.register(heiId, List.of(losIds.get(0)));
    mockProvider2.register(heiId, List.of(losIds.get(1), losIds.get(2)));

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(heiId, SimpleCourseReplicationV1HostProvider.class);
    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, SimpleCourseReplicationV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiSimpleCourseReplicationV1Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    CourseReplicationResponseV1 response = XmlUtils.unmarshall(responseXml,
        CourseReplicationResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getLosId()).hasSize(losIds.size());
    assertThat(response.getLosId().get(0)).isEqualTo(losIds.get(0));
    assertThat(response.getLosId().get(1)).isEqualTo(losIds.get(1));
    assertThat(response.getLosId().get(2)).isEqualTo(losIds.get(2));
  }

  private static class MockSimpleCourseReplicationV1HostProvider extends
      SimpleCourseReplicationV1HostProvider {

    private final Map<String, Collection<String>> heiIdToLosIdsMap = new HashMap<>();

    public void register(String heiId, Collection<String> losIds) {
      this.heiIdToLosIdsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
      this.heiIdToLosIdsMap.get(heiId).addAll(losIds);
    }

    @Override
    public Collection<String> findAllByHeiId(String heiId) {
      return heiIdToLosIdsMap.get(heiId);
    }
  }

}