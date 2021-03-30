package pt.ulisboa.ewp.node.api.ewp.controller.ounits;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2;
import eu.erasmuswithoutpaper.api.ounits.v2.OunitsResponseV2.Ounit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
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
import pt.ulisboa.ewp.host.plugin.skeleton.provider.OrganizationalUnitsHostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

public class EwpApiOrganizationalUnitsControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @Autowired
  private RegistryClient registryClient;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_UnknownHeiId(HttpMethod method) throws Exception {
    String unknownHeiId = "test";

    Mockito.when(hostPluginManager.getProvider(unknownHeiId, OrganizationalUnitsHostProvider.class))
        .thenReturn(Optional.empty());

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, unknownHeiId);
    assertBadRequest(registryClient, method, EwpApiConstants.API_BASE_URI + "ounits", queryParams,
        "Unknown HEI ID: " + unknownHeiId);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndNeitherOunitIdsNorCodes(HttpMethod method)
      throws Exception {
    String heiId = "test";

    MockOrganizationalUnitsHostProvider mockProvider =
        new MockOrganizationalUnitsHostProvider(1, 1);

    Mockito.when(hostPluginManager.getProvider(heiId, OrganizationalUnitsHostProvider.class))
        .thenReturn(Optional.of(mockProvider));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    assertBadRequest(registryClient, method, EwpApiConstants.API_BASE_URI + "ounits", queryParams,
        "At least some organizational unit ID or code must be provided");
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndBothOunitIdsAndCodesProvidedSimultaneously(
      HttpMethod method)
      throws Exception {
    String heiId = "test";

    MockOrganizationalUnitsHostProvider mockProvider =
        new MockOrganizationalUnitsHostProvider(1, 1);

    Mockito.when(hostPluginManager.getProvider(heiId, OrganizationalUnitsHostProvider.class))
        .thenReturn(Optional.of(mockProvider));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.OUNIT_ID, "a");
    queryParams.param(EwpApiParamConstants.OUNIT_CODE, "b");
    assertBadRequest(registryClient, method, EwpApiConstants.API_BASE_URI + "ounits", queryParams,
        "Only organizational unit IDs or codes are accepted, not both simultaneously");
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndExceedingNumberOunitIds(HttpMethod method)
      throws Exception {
    String heiId = "test";

    MockOrganizationalUnitsHostProvider mockProvider =
        new MockOrganizationalUnitsHostProvider(1, 0);

    Mockito.when(hostPluginManager.getProvider(heiId, OrganizationalUnitsHostProvider.class))
        .thenReturn(Optional.of(mockProvider));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.OUNIT_ID, "a");
    queryParams.param(EwpApiParamConstants.OUNIT_ID, "b");
    assertBadRequest(registryClient, method, EwpApiConstants.API_BASE_URI + "ounits", queryParams,
        "Maximum number of valid organizational unit IDs per request is "
            + mockProvider.maxOunitIdsPerRequest);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOunitRetrieval_ValidHeiIdAndExceedingNumberOunitCodes(HttpMethod method)
      throws Exception {
    String heiId = "test";

    MockOrganizationalUnitsHostProvider mockProvider =
        new MockOrganizationalUnitsHostProvider(0, 1);

    Mockito.when(hostPluginManager.getProvider(heiId, OrganizationalUnitsHostProvider.class))
        .thenReturn(Optional.of(mockProvider));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.OUNIT_CODE, "a");
    queryParams.param(EwpApiParamConstants.OUNIT_CODE, "b");
    assertBadRequest(registryClient, method, EwpApiConstants.API_BASE_URI + "ounits", queryParams,
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

    Mockito.when(hostPluginManager.getProvider(validHeiId, OrganizationalUnitsHostProvider.class))
        .thenReturn(Optional.of(mockProvider));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, validHeiId);
    validOunitIds.forEach(ounitId -> queryParams.param(EwpApiParamConstants.OUNIT_ID, ounitId));

    String responseXml =
        executeRequest(registryClient, method, EwpApiConstants.API_BASE_URI + "ounits", queryParams)
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

    Mockito.when(
        hostPluginManager.getProvider(
            validHeiId, OrganizationalUnitsHostProvider.class))
        .thenReturn(Optional.of(mockProvider));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, validHeiId);
    validOunitCodes.forEach(
        ounitCode -> queryParams.param(EwpApiParamConstants.OUNIT_CODE, ounitCode));

    String responseXml =
        executeRequest(registryClient, method, EwpApiConstants.API_BASE_URI + "ounits", queryParams)
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

  private static class MockOrganizationalUnitsHostProvider extends OrganizationalUnitsHostProvider {

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
