package pt.ulisboa.ewp.node.api.ewp.controller.iias;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasGetResponseV6.Iia;
import eu.erasmuswithoutpaper.api.iias.v6.endpoints.IiasIndexResponseV6;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV6HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.MockInterInstitutionalAgreementsV6HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

class EwpApiInterInstitutionalAgreementsV6ControllerTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @Autowired
  private RegistryClient registryClient;

  @SpyBean
  private EwpInterInstitutionalAgreementMappingRepository mappingRepository;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testInterInstitutionalAgreementsIndexRetrieval_UnknownHeiId_ErrorReturned(
      HttpMethod method) throws Exception {
    String heiId = "test";

    doReturn(false).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV6HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);

    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH
            + "/index",
        queryParams,
        "Unknown HEI ID: " + heiId);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testInterInstitutionalAgreementsIndexRetrieval_ValidHeiIdDividedIntoTwoHosts_AllIiaIdsReturned(
      HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> iiaIds = Arrays.asList("a1", "b2", "c3");

    MockInterInstitutionalAgreementsV6HostProvider mockProvider1 = new MockInterInstitutionalAgreementsV6HostProvider(
        3, 0);
    MockInterInstitutionalAgreementsV6HostProvider mockProvider2 = new MockInterInstitutionalAgreementsV6HostProvider(
        3, 0);

    mockProvider1.registerIiaIds(heiId, List.of(iiaIds.get(0)));
    mockProvider2.registerIiaIds(heiId, List.of(iiaIds.get(1), iiaIds.get(2)));

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV6HostProvider.class);
    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(Mockito.anyString(),
            (Class<InterInstitutionalAgreementsV6HostProvider>) Mockito.any(Class.class));

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH
                + "/index",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasIndexResponseV6 response = XmlUtils.unmarshall(responseXml, IiasIndexResponseV6.class);

    assertThat(response).isNotNull();
    assertThat(response.getIiaId()).hasSize(iiaIds.size());
    assertThat(response.getIiaId().get(0)).isEqualTo(iiaIds.get(0));
    assertThat(response.getIiaId().get(1)).isEqualTo(iiaIds.get(1));
    assertThat(response.getIiaId().get(2)).isEqualTo(iiaIds.get(2));
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testInterInstitutionalAgreementsGetRetrievalByIiaIds_ValidHeiIdDividedIntoTwoHostsWithExistingMappings_AllIiasReturned(
      HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> iiaIds = Arrays.asList("a1", "b2", "c3");
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");

    Iia iia1 = new Iia();
    iia1.setConditionsHash(UUID.randomUUID().toString());

    Iia iia2 = new Iia();
    iia2.setConditionsHash(UUID.randomUUID().toString());

    Iia iia3 = new Iia();
    iia3.setConditionsHash(UUID.randomUUID().toString());

    List<Iia> iias = List.of(iia1, iia2, iia3);

    MockInterInstitutionalAgreementsV6HostProvider mockProvider1 = new MockInterInstitutionalAgreementsV6HostProvider(
        3, 0);
    MockInterInstitutionalAgreementsV6HostProvider mockProvider2 = new MockInterInstitutionalAgreementsV6HostProvider(
        3, 0);

    mockProvider1.registerIia(heiId, iiaIds.get(0), UUID.randomUUID().toString(), iia1);

    mockProvider2.registerIia(heiId, iiaIds.get(1), UUID.randomUUID().toString(), iia2);
    mockProvider2.registerIia(heiId, iiaIds.get(2), UUID.randomUUID().toString(), iia3);

    for (int index = 0; index < iiaIds.size(); index++) {
      doReturn(Optional.of(
          EwpInterInstitutionalAgreementMapping.create(heiId, ounitIds.get(index),
              iiaIds.get(index), UUID.randomUUID().toString()))).when(mappingRepository)
          .findByHeiIdAndIiaId(heiId, iiaIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV6HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV6HostProvider.class);

    doReturn(Arrays.asList(mockProvider1)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(0),
            InterInstitutionalAgreementsV6HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(1),
            InterInstitutionalAgreementsV6HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(2),
            InterInstitutionalAgreementsV6HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasGetResponseV6 response = XmlUtils.unmarshall(responseXml, IiasGetResponseV6.class);

    assertThat(response).isNotNull();
    assertThat(response.getIia()).hasSize(iiaIds.size());
    for (Iia iia : iias) {
      assertThat(response.getIia().stream().map(Iia::getConditionsHash)
          .collect(Collectors.toList())).contains(iia.getConditionsHash());
    }
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testInterInstitutionalAgreementsGetRetrievalByIiaIds_ValidHeiIdDividedIntoTwoHostsWithAllButOneExistingMappings_AllKnownIiasReturned(
      HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> iiaIds = Arrays.asList("a1", "b2", "c3");
    List<String> knownIiaIds = Arrays.asList("a1", "b2");
    List<String> ounitIds = Arrays.asList("o1", "o2");

    Iia iia1 = new Iia();
    iia1.setConditionsHash(UUID.randomUUID().toString());

    Iia iia2 = new Iia();
    iia2.setConditionsHash(UUID.randomUUID().toString());

    List<Iia> iias = List.of(iia1, iia2);

    MockInterInstitutionalAgreementsV6HostProvider mockProvider1 = new MockInterInstitutionalAgreementsV6HostProvider(
        3, 0);
    MockInterInstitutionalAgreementsV6HostProvider mockProvider2 = new MockInterInstitutionalAgreementsV6HostProvider(
        3, 0);

    mockProvider1.registerIia(heiId, iiaIds.get(0), UUID.randomUUID().toString(), iia1);

    mockProvider2.registerIia(heiId, iiaIds.get(1), UUID.randomUUID().toString(), iia2);

    for (int index = 0; index < knownIiaIds.size(); index++) {
      doReturn(Optional.of(
          EwpInterInstitutionalAgreementMapping.create(heiId, ounitIds.get(index),
              knownIiaIds.get(index), UUID.randomUUID().toString()))).when(mappingRepository)
          .findByHeiIdAndIiaId(heiId, knownIiaIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV6HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV6HostProvider.class);

    doReturn(Arrays.asList(mockProvider1)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(0),
            InterInstitutionalAgreementsV6HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(1),
            InterInstitutionalAgreementsV6HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasGetResponseV6 response = XmlUtils.unmarshall(responseXml, IiasGetResponseV6.class);

    assertThat(response).isNotNull();
    assertThat(response.getIia()).hasSize(knownIiaIds.size());
    for (Iia iia : iias) {
      assertThat(response.getIia().stream().map(Iia::getConditionsHash)
          .collect(Collectors.toList())).contains(iia.getConditionsHash());
    }
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testInterInstitutionalAgreementsGetRetrievalByIiaCodes_ValidHeiIdDividedIntoTwoHostsWithExistingMappings_AllIiasReturned(
      HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> iiaCodes = Arrays.asList("a1", "b2", "c3");
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");

    Iia iia1 = new Iia();
    iia1.setConditionsHash(UUID.randomUUID().toString());

    Iia iia2 = new Iia();
    iia2.setConditionsHash(UUID.randomUUID().toString());

    Iia iia3 = new Iia();
    iia3.setConditionsHash(UUID.randomUUID().toString());

    List<Iia> iias = List.of(iia1, iia2, iia3);

    MockInterInstitutionalAgreementsV6HostProvider mockProvider1 = new MockInterInstitutionalAgreementsV6HostProvider(
        0, 3);
    MockInterInstitutionalAgreementsV6HostProvider mockProvider2 = new MockInterInstitutionalAgreementsV6HostProvider(
        0, 3);

    mockProvider1.registerIia(heiId, UUID.randomUUID().toString(), iiaCodes.get(0), iia1);

    mockProvider2.registerIia(heiId, UUID.randomUUID().toString(), iiaCodes.get(1), iia2);
    mockProvider2.registerIia(heiId, UUID.randomUUID().toString(), iiaCodes.get(2), iia3);

    for (int index = 0; index < iiaCodes.size(); index++) {
      doReturn(Optional.of(
          EwpInterInstitutionalAgreementMapping.create(heiId, ounitIds.get(index),
              UUID.randomUUID().toString(), iiaCodes.get(index)))).when(mappingRepository)
          .findByHeiIdAndIiaCode(heiId, iiaCodes.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV6HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV6HostProvider.class);

    doReturn(Arrays.asList(mockProvider1)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(0),
            InterInstitutionalAgreementsV6HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(1),
            InterInstitutionalAgreementsV6HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(2),
            InterInstitutionalAgreementsV6HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.IIA_CODE, iiaCodes);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasGetResponseV6 response = XmlUtils.unmarshall(responseXml, IiasGetResponseV6.class);

    assertThat(response).isNotNull();
    assertThat(response.getIia()).hasSize(iiaCodes.size());
    for (Iia iia : iias) {
      assertThat(response.getIia().stream().map(Iia::getConditionsHash)
          .collect(Collectors.toList())).contains(iia.getConditionsHash());
    }
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testInterInstitutionalAgreementsGetRetrievalByIiaCodes_ValidHeiIdDividedIntoTwoHostsWithAllButOneExistingMappings_AllKnownIiasReturned(
      HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> iiaCodes = Arrays.asList("a1", "b2", "c3");
    List<String> knownIiaCodes = Arrays.asList("a1", "b2");
    List<String> ounitIds = Arrays.asList("o1", "o2");

    Iia iia1 = new Iia();
    iia1.setConditionsHash(UUID.randomUUID().toString());

    Iia iia2 = new Iia();
    iia2.setConditionsHash(UUID.randomUUID().toString());

    List<Iia> iias = List.of(iia1, iia2);

    MockInterInstitutionalAgreementsV6HostProvider mockProvider1 = new MockInterInstitutionalAgreementsV6HostProvider(
        0, 3);
    MockInterInstitutionalAgreementsV6HostProvider mockProvider2 = new MockInterInstitutionalAgreementsV6HostProvider(
        0, 3);

    mockProvider1.registerIia(heiId, UUID.randomUUID().toString(), iiaCodes.get(0), iia1);

    mockProvider2.registerIia(heiId, UUID.randomUUID().toString(), iiaCodes.get(1), iia2);

    for (int index = 0; index < knownIiaCodes.size(); index++) {
      doReturn(Optional.of(
          EwpInterInstitutionalAgreementMapping.create(heiId, ounitIds.get(index),
              UUID.randomUUID().toString(), knownIiaCodes.get(index)))).when(mappingRepository)
          .findByHeiIdAndIiaCode(heiId, knownIiaCodes.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV6HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV6HostProvider.class);

    doReturn(Arrays.asList(mockProvider1)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(0),
            InterInstitutionalAgreementsV6HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(heiId, ounitIds.get(1),
            InterInstitutionalAgreementsV6HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.IIA_CODE, iiaCodes);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiInterInstitutionalAgreementsV6Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasGetResponseV6 response = XmlUtils.unmarshall(responseXml, IiasGetResponseV6.class);

    assertThat(response).isNotNull();
    assertThat(response.getIia()).hasSize(knownIiaCodes.size());
    for (Iia iia : iias) {
      assertThat(response.getIia().stream().map(Iia::getConditionsHash)
          .collect(Collectors.toList())).contains(iia.getConditionsHash());
    }
  }

}