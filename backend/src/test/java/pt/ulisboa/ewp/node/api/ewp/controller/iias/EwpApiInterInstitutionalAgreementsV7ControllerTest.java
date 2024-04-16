package pt.ulisboa.ewp.node.api.ewp.controller.iias;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.architecture.v1.StringWithOptionalLangV1;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia.CooperationConditions;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia.Partner;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasIndexResponseV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasStatsResponseV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.StudentStudiesMobilitySpecV7;
import eu.erasmuswithoutpaper.api.types.contact.v1.ContactV1;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.InterInstitutionalAgreementsV7HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.MockInterInstitutionalAgreementsV7HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpInterInstitutionalAgreementMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpInterInstitutionalAgreementMappingRepository;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.service.ewp.iia.hash.v7.IiaHashServiceV7;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

@TestPropertySource(properties = {
    "stats.portal.heiId=test123",
})
class EwpApiInterInstitutionalAgreementsV7ControllerTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @SpyBean
  private EwpInterInstitutionalAgreementMappingRepository mappingRepository;

  @Autowired
  private IiaHashServiceV7 iiaHashService;

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testInterInstitutionalAgreementsIndexRetrieval_UnknownHeiId_ErrorReturned(
      HttpMethod method) throws Exception {
    String heiId = "test";

    Mockito.reset(hostPluginManager);

    doReturn(false).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    assertBadRequest(
        registryClient,
        method,
        EwpApiConstants.API_BASE_URI
            + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
            + "/"
            + heiId
            + "/index",
        new HttpParams(),
        "Unknown HEI ID: " + heiId);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testInterInstitutionalAgreementsIndexRetrieval_ValidHeiIdDividedIntoTwoHosts_AllIiaIdsReturned(
      HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> iiaIds = Arrays.asList("a1", "b2", "c3");

    MockInterInstitutionalAgreementsV7HostProvider mockProvider1 = new MockInterInstitutionalAgreementsV7HostProvider(
        3, 0);
    MockInterInstitutionalAgreementsV7HostProvider mockProvider2 = new MockInterInstitutionalAgreementsV7HostProvider(
        3, 0);

    mockProvider1.registerIiaIds(heiId, List.of(iiaIds.get(0)));
    mockProvider2.registerIiaIds(heiId, List.of(iiaIds.get(1), iiaIds.get(2)));

    Mockito.reset(hostPluginManager);

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV7HostProvider.class);
    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
                    + "/"
                    + heiId
                    + "/index",
                new HttpParams())
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasIndexResponseV7 response = XmlUtils.unmarshall(responseXml, IiasIndexResponseV7.class);

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

    List<Iia> iias = new ArrayList<>();

    for (int index = 0; index < iiaIds.size(); index++) {
      Iia iia = new Iia();
      for (int partnerIndex = 0; partnerIndex < 2; partnerIndex++) {
        Partner partner = new Partner();
        partner.setHeiId(UUID.randomUUID().toString());
        iia.getPartner().add(partner);
      }
      CooperationConditions cooperationConditions = new CooperationConditions();
      StudentStudiesMobilitySpecV7 studentStudiesMobilitySpecV7 = new StudentStudiesMobilitySpecV7();
      studentStudiesMobilitySpecV7.setSendingHeiId(UUID.randomUUID().toString());
      studentStudiesMobilitySpecV7.setReceivingHeiId(UUID.randomUUID().toString());
      studentStudiesMobilitySpecV7.setReceivingOunitId(ounitIds.get(index));
      studentStudiesMobilitySpecV7.setReceivingLastAcademicYearId("2021/2022");
      studentStudiesMobilitySpecV7.setTotalMonthsPerYear(BigDecimal.TEN);
      ContactV1 receivingContact = new ContactV1();
      StringWithOptionalLangV1 stringWithOptionalLangV1 = new StringWithOptionalLangV1();
      stringWithOptionalLangV1.setValue("TEST");
      receivingContact.getContactName().add(stringWithOptionalLangV1);
      studentStudiesMobilitySpecV7.getReceivingContact().add(receivingContact);
      cooperationConditions.getStudentStudiesMobilitySpec().add(studentStudiesMobilitySpecV7);
      iia.setCooperationConditions(cooperationConditions);
      iias.add(iia);
    }

    MockInterInstitutionalAgreementsV7HostProvider mockProvider1 = new MockInterInstitutionalAgreementsV7HostProvider(
        3, 0);
    MockInterInstitutionalAgreementsV7HostProvider mockProvider2 = new MockInterInstitutionalAgreementsV7HostProvider(
        3, 0);

    mockProvider1.registerIia(heiId, iiaIds.get(0), UUID.randomUUID().toString(), iias.get(0));

    mockProvider2.registerIia(heiId, iiaIds.get(1), UUID.randomUUID().toString(), iias.get(1));
    mockProvider2.registerIia(heiId, iiaIds.get(2), UUID.randomUUID().toString(), iias.get(2));

    Mockito.reset(hostPluginManager);

    for (int index = 0; index < iiaIds.size(); index++) {
      doReturn(
              Optional.of(
                  EwpInterInstitutionalAgreementMapping.create(
                      heiId, ounitIds.get(index), iiaIds.get(index))))
          .when(mappingRepository)
          .findByHeiIdAndIiaId(heiId, iiaIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    doReturn(Optional.ofNullable(mockProvider1)).when(hostPluginManager)
        .getSingleProvider(heiId, ounitIds.get(0),
            InterInstitutionalAgreementsV7HostProvider.class);
    doReturn(Optional.ofNullable(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(heiId, ounitIds.get(1),
            InterInstitutionalAgreementsV7HostProvider.class);
    doReturn(Optional.ofNullable(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(heiId, ounitIds.get(2),
            InterInstitutionalAgreementsV7HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
                    + "/"
                    + heiId
                    + "/get",
                queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasGetResponseV7 response = XmlUtils.unmarshall(responseXml, IiasGetResponseV7.class);

    assertThat(response).isNotNull();
    assertThat(response.getIia()).hasSize(iiaIds.size());
    for (Iia iia : response.getIia()) {
      assertThat(iia.getIiaHash()).isEqualTo(
          this.iiaHashService.calculateIiaHashes(List.of(iia)).get(0)
              .getHash());
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

    Iia iia2 = new Iia();

    MockInterInstitutionalAgreementsV7HostProvider mockProvider1 = new MockInterInstitutionalAgreementsV7HostProvider(
        3, 0);
    MockInterInstitutionalAgreementsV7HostProvider mockProvider2 = new MockInterInstitutionalAgreementsV7HostProvider(
        3, 0);

    mockProvider1.registerIia(heiId, iiaIds.get(0), UUID.randomUUID().toString(), iia1);

    mockProvider2.registerIia(heiId, iiaIds.get(1), UUID.randomUUID().toString(), iia2);

    Mockito.reset(hostPluginManager);

    for (int index = 0; index < knownIiaIds.size(); index++) {
      doReturn(
              Optional.of(
                  EwpInterInstitutionalAgreementMapping.create(
                      heiId, ounitIds.get(index), knownIiaIds.get(index))))
          .when(mappingRepository)
          .findByHeiIdAndIiaId(heiId, knownIiaIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    doReturn(Optional.ofNullable(mockProvider1)).when(hostPluginManager)
        .getSingleProvider(heiId, ounitIds.get(0),
            InterInstitutionalAgreementsV7HostProvider.class);
    doReturn(Optional.ofNullable(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(heiId, ounitIds.get(1),
            InterInstitutionalAgreementsV7HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
                    + "/"
                    + heiId
                    + "/get",
                queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasGetResponseV7 response = XmlUtils.unmarshall(responseXml, IiasGetResponseV7.class);

    assertThat(response).isNotNull();
    assertThat(response.getIia()).hasSize(knownIiaIds.size());
    for (Iia iia : response.getIia()) {
      assertThat(iia.getIiaHash()).isEqualTo(
          this.iiaHashService.calculateIiaHashes(List.of(iia)).get(0)
              .getHash());
    }
  }

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"GET", "POST"})
  public void
      testInterInstitutionalAgreementsGetRetrievalByIiaId_ValidHeiIdDividedIntoTwoHostsWithNoExistingMappingsButPrimaryProviderHasIia_IiaReturned(
          HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> iiaIds = List.of("a1");
    List<String> knownIiaIds = List.of("a1");

    Iia iia1 = new Iia();

    MockInterInstitutionalAgreementsV7HostProvider mockProvider1 =
        new MockInterInstitutionalAgreementsV7HostProvider(3, 0);
    MockInterInstitutionalAgreementsV7HostProvider mockProvider2 =
        new MockInterInstitutionalAgreementsV7HostProvider(3, 0);

    mockProvider1.registerIia(heiId, iiaIds.get(0), UUID.randomUUID().toString(), iia1);

    Mockito.reset(hostPluginManager);

    doReturn(List.of(mockProvider1, mockProvider2))
        .when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    doReturn(Optional.of(mockProvider1))
        .when(hostPluginManager)
        .getPrimaryProvider(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
                    + "/"
                    + heiId
                    + "/get",
                queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasGetResponseV7 response = XmlUtils.unmarshall(responseXml, IiasGetResponseV7.class);

    assertThat(response).isNotNull();
    assertThat(response.getIia()).hasSize(knownIiaIds.size());
    for (Iia iia : response.getIia()) {
      assertThat(iia.getIiaHash())
          .isEqualTo(
              this.iiaHashService
                  .calculateIiaHashes(List.of(iia))
                  .get(0)
                  .getHash());
    }
  }

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"GET", "POST"})
  public void
      testInterInstitutionalAgreementsGetRetrievalByIiaId_ValidHeiIdDividedIntoTwoHostsWithNoExistingMappingsButNonPrimaryProviderHasIia_IiaReturned(
          HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> iiaIds = List.of("a1");
    List<String> knownIiaIds = List.of("a1");

    Iia iia1 = new Iia();

    MockInterInstitutionalAgreementsV7HostProvider mockProvider1 =
        new MockInterInstitutionalAgreementsV7HostProvider(3, 0);
    MockInterInstitutionalAgreementsV7HostProvider mockProvider2 =
        new MockInterInstitutionalAgreementsV7HostProvider(3, 0);

    mockProvider2.registerIia(heiId, iiaIds.get(0), UUID.randomUUID().toString(), iia1);

    Mockito.reset(hostPluginManager);

    doReturn(List.of(mockProvider1, mockProvider2))
        .when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    doReturn(Optional.of(mockProvider1))
        .when(hostPluginManager)
        .getPrimaryProvider(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
                    + "/"
                    + heiId
                    + "/get",
                queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasGetResponseV7 response = XmlUtils.unmarshall(responseXml, IiasGetResponseV7.class);

    assertThat(response).isNotNull();
    assertThat(response.getIia()).hasSize(knownIiaIds.size());
    for (Iia iia : response.getIia()) {
      assertThat(iia.getIiaHash())
          .isEqualTo(
              this.iiaHashService
                  .calculateIiaHashes(List.of(iia))
                  .get(0)
                  .getHash());
    }
  }

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"GET", "POST"})
  public void
      testInterInstitutionalAgreementsGetRetrievalByIiaId_ValidHeiIdDividedIntoTwoHostsWithNoExistingMappingsAndNoProviderHasIia_NoIiaReturned(
          HttpMethod method) throws Exception {
    String heiId = "test";
    List<String> iiaIds = List.of("a1");

    MockInterInstitutionalAgreementsV7HostProvider mockProvider1 =
        new MockInterInstitutionalAgreementsV7HostProvider(3, 0);
    MockInterInstitutionalAgreementsV7HostProvider mockProvider2 =
        new MockInterInstitutionalAgreementsV7HostProvider(3, 0);

    Mockito.reset(hostPluginManager);

    doReturn(List.of(mockProvider1, mockProvider2))
        .when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    doReturn(Optional.of(mockProvider1))
        .when(hostPluginManager)
        .getPrimaryProvider(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaIds);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
                    + "/"
                    + heiId
                    + "/get",
                queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiasGetResponseV7 response = XmlUtils.unmarshall(responseXml, IiasGetResponseV7.class);

    assertThat(response).isNotNull();
    assertThat(response.getIia()).hasSize(0);
  }

  @Test
  public void testStatsRetrieval_InvalidHeiId_ErrorReturned()
      throws Exception {
    String invalidHeiId = UUID.randomUUID().toString();

    assertErrorRequest(
        registryClient,
        HttpMethod.GET,
        EwpApiConstants.API_BASE_URI
            + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
            + "/"
            + invalidHeiId
            + "/stats",
        new HttpParams(),
        HttpStatus.BAD_REQUEST,
        new Condition<>(
            errorResponse ->
                errorResponse.getDeveloperMessage().getValue().contains("Unauthorized HEI ID"),
            "unauthorized HEI ID"));
  }

  @Test
  public void testStatsRetrieval_ValidRequesterHeiIdAndTwoHostProviders_StatsMergedReturned()
      throws Exception {
    String heiId = UUID.randomUUID().toString();

    MockInterInstitutionalAgreementsV7HostProvider mockProvider1 = new MockInterInstitutionalAgreementsV7HostProvider(
        3, 3);

    IiasStatsResponseV7 stats1 = new IiasStatsResponseV7();
    stats1.setIiaFetchable(BigInteger.valueOf(1L));
    stats1.setIiaLocalUnapprovedPartnerApproved(BigInteger.valueOf(2L));
    stats1.setIiaLocalApprovedPartnerUnapproved(BigInteger.valueOf(3L));
    stats1.setIiaBothApproved(BigInteger.valueOf(4L));

    mockProvider1.registerStats(heiId, stats1);

    MockInterInstitutionalAgreementsV7HostProvider mockProvider2 = new MockInterInstitutionalAgreementsV7HostProvider(
        3, 3);

    IiasStatsResponseV7 stats2 = new IiasStatsResponseV7();
    stats2.setIiaFetchable(BigInteger.valueOf(11L));
    stats2.setIiaLocalUnapprovedPartnerApproved(BigInteger.valueOf(12L));
    stats2.setIiaLocalApprovedPartnerUnapproved(BigInteger.valueOf(13L));
    stats2.setIiaBothApproved(BigInteger.valueOf(14L));

    mockProvider2.registerStats(heiId, stats2);

    Mockito.reset(hostPluginManager);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, InterInstitutionalAgreementsV7HostProvider.class);

    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.request(
            HttpMethod.GET,
            EwpApiConstants.API_BASE_URI
                + EwpApiInterInstitutionalAgreementsV7Controller.BASE_PATH
                + "/"
                + heiId
                + "/stats");

    String responseXml = executeRequest(registryClient, requestBuilder,
        httpSignatureRequestProcessor(registryClient, List.of("test123")))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    IiasStatsResponseV7 response = XmlUtils.unmarshall(responseXml,
        IiasStatsResponseV7.class);

    assertThat(response).isNotNull();
    assertThat(response.getIiaFetchable()).isEqualTo(
        BigInteger.valueOf(12L));
    assertThat(response.getIiaLocalUnapprovedPartnerApproved()).isEqualTo(
        BigInteger.valueOf(14L));
    assertThat(response.getIiaLocalApprovedPartnerUnapproved()).isEqualTo(
        BigInteger.valueOf(16L));
    assertThat(response.getIiaBothApproved()).isEqualTo(
        BigInteger.valueOf(18L));
  }

}