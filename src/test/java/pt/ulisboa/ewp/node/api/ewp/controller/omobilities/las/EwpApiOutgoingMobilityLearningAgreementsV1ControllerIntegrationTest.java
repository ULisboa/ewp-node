package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.las;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.architecture.v1.MultilineStringWithOptionalLangV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.ApproveProposalV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.LearningAgreementV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.MobilityInstitutionV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasIndexResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateRequestV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateResponseV1;
import java.util.ArrayList;
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
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.MockOutgoingMobilityLearningAgreementsV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.OutgoingMobilityLearningAgreementsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpOutgoingMobilityMappingRepository;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.XmlUtils;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

class EwpApiOutgoingMobilityLearningAgreementsV1ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @Autowired
  private RegistryClient registryClient;

  @SpyBean
  private EwpOutgoingMobilityMappingRepository mappingRepository;

  @ParameterizedTest
  @EnumSource(
      value = HttpMethod.class,
      names = {"GET", "POST"})
  public void testOutgoingMobilityIdsRetrieval_UnknownSendingHeiId_ErrorReturned(HttpMethod method)
      throws Exception {
    String unknownHeiId = UUID.randomUUID().toString();

    Mockito
        .when(hostPluginManager.getProvider(unknownHeiId,
            OutgoingMobilityLearningAgreementsV1HostProvider.class))
        .thenReturn(Optional.empty());

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, unknownHeiId);

    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI
            + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH
            + "/index", queryParams, "Unknown HEI ID: " + unknownHeiId);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOutgoingMobilityIdsRetrieval_ValidSendingHeiIdDividedIntoTwoHosts_AllOmobilityIdsReturned(
      HttpMethod method) throws Exception {
    String sendingHeiId = "test";
    List<String> omobilityIds = Arrays.asList("a1", "b2", "c3");

    MockOutgoingMobilityLearningAgreementsV1HostProvider mockProvider1 = new MockOutgoingMobilityLearningAgreementsV1HostProvider(
        3);
    MockOutgoingMobilityLearningAgreementsV1HostProvider mockProvider2 = new MockOutgoingMobilityLearningAgreementsV1HostProvider(
        3);

    mockProvider1.registerOutgoingMobilityIds(sendingHeiId, List.of(omobilityIds.get(0)));
    mockProvider2.registerOutgoingMobilityIds(sendingHeiId,
        List.of(omobilityIds.get(1), omobilityIds.get(2)));

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(sendingHeiId, OutgoingMobilityLearningAgreementsV1HostProvider.class);
    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(sendingHeiId,
            OutgoingMobilityLearningAgreementsV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH
                + "/index",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilityLasIndexResponseV1 response = XmlUtils.unmarshall(responseXml,
        OmobilityLasIndexResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getOmobilityId()).isEqualTo(omobilityIds);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOutgoingMobilityLearningAgreementsGetRetrievalByOmobilityIds_ValidHeiIdDividedIntoTwoHostsWithExistingMappings_AllOutgoingMobilitiesReturned(
      HttpMethod method) throws Exception {
    String sendingHeiId = "test";
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");
    List<String> omobilityIds = Arrays.asList("a1", "b2", "c3");

    List<LearningAgreementV1> learningAgreements = new ArrayList<>();
    for (int index = 0; index < omobilityIds.size(); index++) {
      LearningAgreementV1 learningAgreement = new LearningAgreementV1();
      learningAgreement.setOmobilityId(omobilityIds.get(index));
      MobilityInstitutionV1 sendingHei = new MobilityInstitutionV1();
      sendingHei.setHeiId(sendingHeiId);
      sendingHei.setOunitId(ounitIds.get(index));
      learningAgreement.setSendingHei(sendingHei);
      learningAgreements.add(learningAgreement);
    }

    MockOutgoingMobilityLearningAgreementsV1HostProvider mockProvider1 = new MockOutgoingMobilityLearningAgreementsV1HostProvider(
        3);
    MockOutgoingMobilityLearningAgreementsV1HostProvider mockProvider2 = new MockOutgoingMobilityLearningAgreementsV1HostProvider(
        3);

    mockProvider1.registerLearningAgreement(sendingHeiId, omobilityIds.get(0),
        learningAgreements.get(0));

    mockProvider2.registerLearningAgreement(sendingHeiId, omobilityIds.get(1),
        learningAgreements.get(1));
    mockProvider2.registerLearningAgreement(sendingHeiId, omobilityIds.get(2),
        learningAgreements.get(2));

    for (int index = 0; index < omobilityIds.size(); index++) {
      doReturn(Optional.of(
          EwpOutgoingMobilityMapping.create(sendingHeiId, ounitIds.get(index),
              omobilityIds.get(index)))).when(mappingRepository)
          .findByHeiIdAndOmobilityId(sendingHeiId, omobilityIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(sendingHeiId, OutgoingMobilityLearningAgreementsV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(sendingHeiId,
            OutgoingMobilityLearningAgreementsV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider1)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(sendingHeiId, ounitIds.get(0),
            OutgoingMobilityLearningAgreementsV1HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(sendingHeiId, ounitIds.get(1),
            OutgoingMobilityLearningAgreementsV1HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(sendingHeiId, ounitIds.get(2),
            OutgoingMobilityLearningAgreementsV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilityLasGetResponseV1 response = XmlUtils.unmarshall(responseXml,
        OmobilityLasGetResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getLa()).hasSize(omobilityIds.size());
    for (LearningAgreementV1 learningAgreement : learningAgreements) {
      assertThat(response.getLa().stream()
          .map(LearningAgreementV1::getOmobilityId)
          .collect(Collectors.toList())).contains(learningAgreement.getOmobilityId());
    }
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOutgoingMobilityLearningAgreementsGetRetrievalByOmobilityIds_ValidHeiIdDividedIntoTwoHostsWithAllButOneExistingMappings_AllKnownIiasReturned(
      HttpMethod method) throws Exception {
    String sendingHeiId = "test";
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");
    List<String> omobilityIds = Arrays.asList("a1", "b2", "c3");
    List<String> knownOmobilityIds = omobilityIds.subList(0, 2);

    List<LearningAgreementV1> learningAgreements = new ArrayList<>();
    for (int index = 0; index < knownOmobilityIds.size(); index++) {
      LearningAgreementV1 learningAgreement = new LearningAgreementV1();
      learningAgreement.setOmobilityId(knownOmobilityIds.get(index));
      MobilityInstitutionV1 sendingHei = new MobilityInstitutionV1();
      sendingHei.setHeiId(sendingHeiId);
      sendingHei.setOunitId(ounitIds.get(index));
      learningAgreement.setSendingHei(sendingHei);
      learningAgreements.add(learningAgreement);
    }

    MockOutgoingMobilityLearningAgreementsV1HostProvider mockProvider1 = new MockOutgoingMobilityLearningAgreementsV1HostProvider(
        3);
    MockOutgoingMobilityLearningAgreementsV1HostProvider mockProvider2 = new MockOutgoingMobilityLearningAgreementsV1HostProvider(
        3);

    mockProvider1.registerLearningAgreement(sendingHeiId, omobilityIds.get(0),
        learningAgreements.get(0));

    mockProvider2.registerLearningAgreement(sendingHeiId, omobilityIds.get(1),
        learningAgreements.get(1));

    for (int index = 0; index < knownOmobilityIds.size(); index++) {
      doReturn(Optional.of(
          EwpOutgoingMobilityMapping.create(sendingHeiId, ounitIds.get(index),
              knownOmobilityIds.get(index)))).when(mappingRepository)
          .findByHeiIdAndOmobilityId(sendingHeiId, knownOmobilityIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(sendingHeiId, OutgoingMobilityLearningAgreementsV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(sendingHeiId,
            OutgoingMobilityLearningAgreementsV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider1)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(sendingHeiId, ounitIds.get(0),
            OutgoingMobilityLearningAgreementsV1HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(sendingHeiId, ounitIds.get(1),
            OutgoingMobilityLearningAgreementsV1HostProvider.class);
    doReturn(Arrays.asList(mockProvider2)).when(hostPluginManager)
        .getProvidersByHeiIdAndOunitId(sendingHeiId, ounitIds.get(2),
            OutgoingMobilityLearningAgreementsV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilityLasGetResponseV1 response = XmlUtils.unmarshall(responseXml,
        OmobilityLasGetResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getLa()).hasSize(knownOmobilityIds.size());
    for (LearningAgreementV1 learningAgreement : learningAgreements) {
      assertThat(response.getLa().stream()
          .map(LearningAgreementV1::getOmobilityId)
          .collect(Collectors.toList())).contains(learningAgreement.getOmobilityId());
    }
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"POST"})
  public void testOutgoingMobilityLearningAgreementsUpdate_ValidHeiIdDividedIntoTwoHostsWithExistingMapping_CorrectHostProviderIsCalled(
      HttpMethod method) throws Exception {
    String sendingHeiId = "test";
    String ounitId = "o1";
    String omobilityId = "om1";

    MockOutgoingMobilityLearningAgreementsV1HostProvider mockProvider = new MockOutgoingMobilityLearningAgreementsV1HostProvider(
        3);

    OmobilityLasUpdateRequestV1 updateData = new OmobilityLasUpdateRequestV1();
    updateData.setSendingHeiId(sendingHeiId);
    ApproveProposalV1 approveProposal = new ApproveProposalV1();
    approveProposal.setOmobilityId(omobilityId);
    updateData.setApproveProposalV1(approveProposal);

    OmobilityLasUpdateResponseV1 expectedResponse = new OmobilityLasUpdateResponseV1();
    MultilineStringWithOptionalLangV1 multilineStringWithOptionalLangV1 = new MultilineStringWithOptionalLangV1();
    multilineStringWithOptionalLangV1.setValue("TEST");
    expectedResponse.getSuccessUserMessage().add(multilineStringWithOptionalLangV1);

    mockProvider.registerUpdateDataToResponse(updateData, expectedResponse);

    doReturn(Optional.of(
        EwpOutgoingMobilityMapping.create(sendingHeiId, ounitId,
            omobilityId))).when(mappingRepository)
        .findByHeiIdAndOmobilityId(sendingHeiId, omobilityId);

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(sendingHeiId, OutgoingMobilityLearningAgreementsV1HostProvider.class);

    doReturn(Optional.of(mockProvider)).when(hostPluginManager)
        .getProvider(sendingHeiId, ounitId, OutgoingMobilityLearningAgreementsV1HostProvider.class);

    String responseXml = executeRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI
            + EwpApiOutgoingMobilityLearningAgreementsV1Controller.BASE_PATH
            + "/update", updateData)
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    OmobilityLasUpdateResponseV1 response = XmlUtils.unmarshall(responseXml,
        OmobilityLasUpdateResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getSuccessUserMessage()).hasSize(1);
    assertThat(response.getSuccessUserMessage().get(0).getValue()).isEqualTo(
        expectedResponse.getSuccessUserMessage().get(0).getValue());
  }

}