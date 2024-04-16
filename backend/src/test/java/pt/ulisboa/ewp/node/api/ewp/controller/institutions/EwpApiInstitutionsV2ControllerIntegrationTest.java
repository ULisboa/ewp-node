package pt.ulisboa.ewp.node.api.ewp.controller.institutions;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2;
import eu.erasmuswithoutpaper.api.institutions.v2.InstitutionsResponseV2.Hei;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Condition;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.institutions.InstitutionsV2HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.ounits.OrganizationalUnitsV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

public class EwpApiInstitutionsV2ControllerIntegrationTest
    extends AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"GET", "POST"})
  public void testInstitutionRetrieval_OneUnknownHeiId(HttpMethod method) throws Exception {
    String unknownHeiId = UUID.randomUUID().toString();

    Mockito
        .when(hostPluginManager.getPrimaryProvider(unknownHeiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(Optional.empty());

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, unknownHeiId);

    String responseXml =
        executeRequest(
            registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiInstitutionsV2Controller.BASE_PATH, queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    InstitutionsResponseV2 response =
        XmlUtils.unmarshall(responseXml, InstitutionsResponseV2.class);

    assertThat(response).isNotNull();
    assertThat(response.getHei()).hasSize(0);
  }

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"GET", "POST"})
  public void testInstitutionRetrieval_OneValidHeiId(HttpMethod method) throws Exception {
    String validHeiId = UUID.randomUUID().toString();

    MockInstitutionsV2HostProvider mockProvider = new MockInstitutionsV2HostProvider();

    Hei hei = new Hei();
    hei.setHeiId(validHeiId);
    hei.setAbbreviation("TEST");
    mockProvider.register(hei);

    Mockito.when(hostPluginManager.getPrimaryProvider(validHeiId, InstitutionsV2HostProvider.class))
        .thenReturn(Optional.of(mockProvider));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, validHeiId);

    String responseXml =
        executeRequest(
            registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiInstitutionsV2Controller.BASE_PATH, queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    InstitutionsResponseV2 response =
        XmlUtils.unmarshall(responseXml, InstitutionsResponseV2.class);

    assertThat(response).isNotNull();
    assertThat(response.getHei()).hasSize(1);
    assertThat(response.getHei().get(0).getHeiId()).isEqualTo(hei.getHeiId());
    assertThat(response.getHei().get(0).getAbbreviation()).isEqualTo(hei.getAbbreviation());
  }

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"GET", "POST"})
  public void testInstitutionRetrieval_MoreThanLimit(HttpMethod method) throws Exception {
    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, UUID.randomUUID().toString());
    queryParams.param(EwpApiParamConstants.HEI_ID, UUID.randomUUID().toString());
    assertBadRequest(
        registryClient,
        method,
        EwpApiConstants.API_BASE_URI + EwpApiInstitutionsV2Controller.BASE_PATH,
        queryParams,
        "Maximum number of valid HEI IDs per request is 1");
  }

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"PUT", "DELETE"})
  public void testInstitutionRetrieval_UnsupportedHttpMethod(HttpMethod method) throws Exception {
    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, UUID.randomUUID().toString());
    assertErrorRequest(
        registryClient,
        method,
        EwpApiConstants.API_BASE_URI + EwpApiInstitutionsV2Controller.BASE_PATH,
        queryParams,
        HttpStatus.METHOD_NOT_ALLOWED,
        new Condition<>(errorResponse -> true, "valid developer message"));
  }

  private static class MockInstitutionsV2HostProvider extends InstitutionsV2HostProvider {

    private final Map<String, Hei> heiIdToHeiMap = new HashMap<>();

    public void register(Hei hei) {
      this.heiIdToHeiMap.put(hei.getHeiId(), hei);
    }

    @Override
    public Optional<Hei> findByHeiId(String heiId) {
      return Optional.ofNullable(this.heiIdToHeiMap.get(heiId));
    }
  }
}
