package pt.ulisboa.ewp.node.api.ewp.controller.ounits;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2;
import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2.Ounit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.ounits.OrganizationalUnitsV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

public class EwpApiOrganizationalUnitsV2ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @Autowired
  private RegistryClient registryClient;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_UnknownHeiId(HttpMethod method) throws Exception {
    String unknownHeiId = "test";

    Mockito
        .when(hostPluginManager.hasHostProvider(unknownHeiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(false);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, unknownHeiId);
    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH, queryParams,
        "Unknown HEI ID: " + unknownHeiId);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndNeitherOunitIdsNorCodes(HttpMethod method)
      throws Exception {
    String heiId = "test";

    MockOrganizationalUnitsHostProvider mockProvider =
        new MockOrganizationalUnitsHostProvider(1, 1);

    Mockito
        .when(hostPluginManager.hasHostProvider(heiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(true);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH, queryParams,
        "At least some organizational unit ID or code must be provided");
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndBothOunitIdsAndCodesProvidedSimultaneously(
      HttpMethod method)
      throws Exception {
    String heiId = "test";

    Mockito
        .when(hostPluginManager.hasHostProvider(heiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(true);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.OUNIT_ID, "a");
    queryParams.param(EwpApiParamConstants.OUNIT_CODE, "b");
    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH, queryParams,
        "Only organizational unit IDs or codes are accepted, not both simultaneously");
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndExceedingNumberOunitIds(HttpMethod method)
      throws Exception {
    String heiId = "test";
    List<String> ounitIds = Arrays.asList("a", "b");

    MockOrganizationalUnitsHostProvider mockProvider =
        new MockOrganizationalUnitsHostProvider(1, 0);

    Map<OrganizationalUnitsV2HostProvider, Collection<String>> providerToOunitIdsMap = Map.of(
        mockProvider, Collections.emptyList()
    );

    Mockito
        .when(hostPluginManager.hasHostProvider(heiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(true);
    doReturn(providerToOunitIdsMap).when(hostPluginManager)
        .getOunitIdsCoveredPerProviderOfHeiId(heiId, ounitIds,
            OrganizationalUnitsV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.OUNIT_ID, ounitIds);
    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH, queryParams,
        "Maximum number of valid organizational unit IDs per request is "
            + mockProvider.maxOunitIdsPerRequest);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndExceedingNumberOunitCodes(HttpMethod method)
      throws Exception {
    String heiId = "test";
    List<String> ounitCodes = Arrays.asList("a", "b");

    MockOrganizationalUnitsHostProvider mockProvider =
        new MockOrganizationalUnitsHostProvider(0, 1);

    Mockito
        .when(hostPluginManager.hasHostProvider(heiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(true);
    Mockito.when(
            hostPluginManager.getPrimaryProvider(heiId, OrganizationalUnitsV2HostProvider.class))
        .thenReturn(Optional.of(mockProvider));

    Map<OrganizationalUnitsV2HostProvider, Collection<String>> providerToOunitCodesMap = Map.of(
        mockProvider, Collections.emptyList()
    );
    doReturn(providerToOunitCodesMap).when(hostPluginManager)
        .getOunitCodesCoveredPerProviderOfHeiId(heiId, ounitCodes,
            OrganizationalUnitsV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.OUNIT_CODE, "a");
    queryParams.param(EwpApiParamConstants.OUNIT_CODE, "b");
    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH, queryParams,
        "Maximum number of valid organizational unit codes per request is "
            + mockProvider.maxOunitCodesPerRequest);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndTwoValidOunitIds(HttpMethod method) throws Exception {
    String validHeiId = "test";
    List<String> validOunitIds = Arrays.asList("a1", "b2");

    MockOrganizationalUnitsHostProvider mockProvider =
        new MockOrganizationalUnitsHostProvider(2, 0);

    Ounit ounit1 = new Ounit();
    ounit1.setOunitId(validOunitIds.get(0));
    mockProvider.register(validHeiId, ounit1);

    Ounit ounit2 = new Ounit();
    ounit2.setOunitId(validOunitIds.get(1));
    mockProvider.register(validHeiId, ounit2);

    Map<OrganizationalUnitsV2HostProvider, Collection<String>> providerToOunitIdsMap = Map.of(
        mockProvider, List.of(ounit1.getOunitId(), ounit2.getOunitId())
    );

    Mockito
        .when(hostPluginManager.hasHostProvider(validHeiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(true);
    doReturn(providerToOunitIdsMap).when(hostPluginManager)
        .getOunitIdsCoveredPerProviderOfHeiId(validHeiId, validOunitIds,
            OrganizationalUnitsV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, validHeiId);
    validOunitIds.forEach(ounitId -> queryParams.param(EwpApiParamConstants.OUNIT_ID, ounitId));

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OunitsResponseV2 response = XmlUtils.unmarshall(responseXml, OunitsResponseV2.class);

    assertThat(response).isNotNull();
    assertThat(response.getOunit()).hasSize(validOunitIds.size());
    assertThat(response.getOunit().get(0).getOunitId()).isEqualTo(validOunitIds.get(0));
    assertThat(response.getOunit().get(1).getOunitId()).isEqualTo(validOunitIds.get(1));
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndTwoValidOunitCodes(HttpMethod method)
      throws Exception {
    String validHeiId = "test";
    List<String> validOunitCodes = Arrays.asList("a1", "b2");

    MockOrganizationalUnitsHostProvider mockProvider =
        new MockOrganizationalUnitsHostProvider(0, 2);

    Ounit ounit1 = new Ounit();
    ounit1.setOunitCode(validOunitCodes.get(0));
    mockProvider.register(validHeiId, ounit1);

    Ounit ounit2 = new Ounit();
    ounit2.setOunitCode(validOunitCodes.get(1));
    mockProvider.register(validHeiId, ounit2);

    Map<OrganizationalUnitsV2HostProvider, Collection<String>> providerToOunitCodesMap = Map.of(
        mockProvider, List.of(ounit1.getOunitCode(), ounit2.getOunitCode())
    );

    Mockito
        .when(hostPluginManager.hasHostProvider(validHeiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(true);
    doReturn(providerToOunitCodesMap).when(hostPluginManager)
        .getOunitCodesCoveredPerProviderOfHeiId(validHeiId, validOunitCodes,
            OrganizationalUnitsV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, validHeiId);
    validOunitCodes.forEach(
        ounitCode -> queryParams.param(EwpApiParamConstants.OUNIT_CODE, ounitCode));

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OunitsResponseV2 response = XmlUtils.unmarshall(responseXml, OunitsResponseV2.class);

    assertThat(response).isNotNull();
    assertThat(response.getOunit()).hasSize(validOunitCodes.size());
    assertThat(response.getOunit().get(0).getOunitCode()).isEqualTo(validOunitCodes.get(0));
    assertThat(response.getOunit().get(1).getOunitCode()).isEqualTo(validOunitCodes.get(1));
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndThreeValidOunitIdsDividedIntoTwoHosts(
      HttpMethod method) throws Exception {
    String validHeiId = "test";
    List<String> validOunitIds = Arrays.asList("a1", "b2", "c3");

    MockOrganizationalUnitsHostProvider mockProvider1 =
        new MockOrganizationalUnitsHostProvider(3, 0);

    MockOrganizationalUnitsHostProvider mockProvider2 =
        new MockOrganizationalUnitsHostProvider(3, 0);

    Ounit ounit1 = new Ounit();
    ounit1.setOunitId(validOunitIds.get(0));
    mockProvider1.register(validHeiId, ounit1);

    Ounit ounit2 = new Ounit();
    ounit2.setOunitId(validOunitIds.get(1));
    mockProvider2.register(validHeiId, ounit2);

    Ounit ounit3 = new Ounit();
    ounit3.setOunitId(validOunitIds.get(2));
    mockProvider2.register(validHeiId, ounit3);

    Map<OrganizationalUnitsV2HostProvider, Collection<String>> providerToOunitIdsMap = Map.of(
        mockProvider1, List.of(ounit1.getOunitId()),
        mockProvider2, List.of(ounit2.getOunitId(), ounit3.getOunitId())
    );

    Mockito
        .when(hostPluginManager.hasHostProvider(validHeiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(true);
    doReturn(providerToOunitIdsMap).when(hostPluginManager)
        .getOunitIdsCoveredPerProviderOfHeiId(validHeiId, validOunitIds,
            OrganizationalUnitsV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, validHeiId);
    validOunitIds.forEach(ounitId -> queryParams.param(EwpApiParamConstants.OUNIT_ID, ounitId));

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OunitsResponseV2 response = XmlUtils.unmarshall(responseXml, OunitsResponseV2.class);

    assertThat(response).isNotNull();
    assertThat(response.getOunit()).hasSize(validOunitIds.size());
    assertThat(response.getOunit().get(0).getOunitId()).isEqualTo(validOunitIds.get(0));
    assertThat(response.getOunit().get(1).getOunitId()).isEqualTo(validOunitIds.get(1));
    assertThat(response.getOunit().get(2).getOunitId()).isEqualTo(validOunitIds.get(2));
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndThreeValidOunitCodesDividedIntoTwoHosts(
      HttpMethod method) throws Exception {
    String validHeiId = "test";
    List<String> validOunitCodes = Arrays.asList("a1", "b2", "c3");

    MockOrganizationalUnitsHostProvider mockProvider1 =
        new MockOrganizationalUnitsHostProvider(0, 3);

    MockOrganizationalUnitsHostProvider mockProvider2 =
        new MockOrganizationalUnitsHostProvider(0, 3);

    Ounit ounit1 = new Ounit();
    ounit1.setOunitCode(validOunitCodes.get(0));
    mockProvider1.register(validHeiId, ounit1);

    Ounit ounit2 = new Ounit();
    ounit2.setOunitCode(validOunitCodes.get(1));
    mockProvider2.register(validHeiId, ounit2);

    Ounit ounit3 = new Ounit();
    ounit3.setOunitCode(validOunitCodes.get(2));
    mockProvider2.register(validHeiId, ounit3);

    Map<OrganizationalUnitsV2HostProvider, Collection<String>> providerToOunitCodesMap = Map.of(
        mockProvider1, List.of(ounit1.getOunitCode()),
        mockProvider2, List.of(ounit2.getOunitCode(), ounit3.getOunitCode())
    );

    Mockito
        .when(hostPluginManager.hasHostProvider(validHeiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(true);
    doReturn(providerToOunitCodesMap).when(hostPluginManager)
        .getOunitCodesCoveredPerProviderOfHeiId(validHeiId, validOunitCodes,
            OrganizationalUnitsV2HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, validHeiId);
    validOunitCodes.forEach(
        ounitCode -> queryParams.param(EwpApiParamConstants.OUNIT_CODE, ounitCode));

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiOrganizationalUnitsV2Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OunitsResponseV2 response = XmlUtils.unmarshall(responseXml, OunitsResponseV2.class);

    assertThat(response).isNotNull();
    assertThat(response.getOunit()).hasSize(validOunitCodes.size());
    assertThat(response.getOunit().get(0).getOunitCode()).isEqualTo(validOunitCodes.get(0));
    assertThat(response.getOunit().get(1).getOunitCode()).isEqualTo(validOunitCodes.get(1));
    assertThat(response.getOunit().get(2).getOunitCode()).isEqualTo(validOunitCodes.get(2));
  }

  private static class MockOrganizationalUnitsHostProvider extends
      OrganizationalUnitsV2HostProvider {

    private final int maxOunitIdsPerRequest;
    private final int maxOunitCodesPerRequest;

    private final Map<String, Collection<Ounit>> heiIdToOrganizationalUnitsMap = new HashMap<>();

    MockOrganizationalUnitsHostProvider(int maxOunitIdsPerRequest, int maxOunitCodesPerRequest) {
      this.maxOunitIdsPerRequest = maxOunitIdsPerRequest;
      this.maxOunitCodesPerRequest = maxOunitCodesPerRequest;
    }

    public void register(String heiId, Ounit ounit) {
      this.heiIdToOrganizationalUnitsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
      this.heiIdToOrganizationalUnitsMap.get(heiId).add(ounit);
    }

    @Override
    public Collection<Ounit> findByHeiIdAndOunitIds(String heiId, Collection<String> ounitIds) {
      this.heiIdToOrganizationalUnitsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
      return heiIdToOrganizationalUnitsMap.get(heiId).stream()
          .filter(o -> ounitIds.contains(o.getOunitId()))
          .collect(Collectors.toList());
    }

    @Override
    public Collection<Ounit> findByHeiIdAndOunitCodes(String heiId, Collection<String> ounitCodes) {
      this.heiIdToOrganizationalUnitsMap.computeIfAbsent(heiId, h -> new ArrayList<>());
      return heiIdToOrganizationalUnitsMap.get(heiId).stream()
          .filter(o -> ounitCodes.contains(o.getOunitCode()))
          .collect(Collectors.toList());
    }

    @Override
    public int getMaxOunitIdsPerRequest() {
      return maxOunitIdsPerRequest;
    }

    @Override
    public int getMaxOunitCodesPerRequest() {
      return maxOunitCodesPerRequest;
    }
  }
}
