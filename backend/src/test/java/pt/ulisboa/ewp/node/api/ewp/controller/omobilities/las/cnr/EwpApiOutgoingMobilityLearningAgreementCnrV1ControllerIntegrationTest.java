package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.las.cnr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.LasIncomingStatsResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.LasIncomingStatsResponseV1.AcademicYearLaStats;
import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrResponseV1;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.cnr.MockOutgoingMobilityLearningAgreementCnrV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.las.cnr.OutgoingMobilityLearningAgreementCnrV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.tests.provider.argument.HttpGetAndPostArgumentProvider;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

@TestPropertySource(properties = {
    "stats.portal.heiId=test123",
})
class EwpApiOutgoingMobilityLearningAgreementCnrV1ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  void testOutgoingMobilityLearningAgreementCnr_TwoAdmissibleHostProviders_BothHostProvidersInvoked(
      HttpMethod method) throws Exception {
    String sendingHeiId = "test";
    String omobilityId = "om1";

    MockOutgoingMobilityLearningAgreementCnrV1HostProvider mockProvider1 = Mockito.spy(
        new MockOutgoingMobilityLearningAgreementCnrV1HostProvider());
    MockOutgoingMobilityLearningAgreementCnrV1HostProvider mockProvider2 = Mockito.spy(
        new MockOutgoingMobilityLearningAgreementCnrV1HostProvider());

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(OutgoingMobilityLearningAgreementCnrV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI
                + EwpApiOutgoingMobilityLearningAgreementCnrV1Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilityLaCnrResponseV1 response = XmlUtils.unmarshall(responseXml,
        OmobilityLaCnrResponseV1.class);

    assertThat(response).isNotNull();

    verify(mockProvider1, times(1)).onChangeNotification(sendingHeiId, List.of(omobilityId));
    verify(mockProvider2, times(1)).onChangeNotification(sendingHeiId, List.of(omobilityId));
  }

  @Test
  public void testStatsRetrieval_InvalidHeiId_ErrorReturned()
      throws Exception {
    String invalidHeiId = UUID.randomUUID().toString();

    assertErrorRequest(
        registryClient,
        HttpMethod.GET,
        EwpApiConstants.API_BASE_URI
            + EwpApiOutgoingMobilityLearningAgreementCnrV1Controller.BASE_PATH
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
    String receivingAcademicYear = "0000/0001";

    MockOutgoingMobilityLearningAgreementCnrV1HostProvider mockProvider1 = new MockOutgoingMobilityLearningAgreementCnrV1HostProvider();

    LasIncomingStatsResponseV1 stats1 = new LasIncomingStatsResponseV1();
    AcademicYearLaStats academicYearLaStats1 = new AcademicYearLaStats();
    academicYearLaStats1.setReceivingAcademicYearId(receivingAcademicYear);
    academicYearLaStats1.setLaIncomingTotal(BigInteger.ONE);
    stats1.getAcademicYearLaStats().add(academicYearLaStats1);

    mockProvider1.registerStats(heiId, stats1);

    MockOutgoingMobilityLearningAgreementCnrV1HostProvider mockProvider2 = new MockOutgoingMobilityLearningAgreementCnrV1HostProvider();

    LasIncomingStatsResponseV1 stats2 = new LasIncomingStatsResponseV1();
    AcademicYearLaStats academicYearLaStats2 = new AcademicYearLaStats();
    academicYearLaStats2.setReceivingAcademicYearId(receivingAcademicYear);
    academicYearLaStats2.setLaIncomingTotal(BigInteger.TWO);
    stats2.getAcademicYearLaStats().add(academicYearLaStats2);

    mockProvider2.registerStats(heiId, stats2);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, OutgoingMobilityLearningAgreementCnrV1HostProvider.class);

    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.request(
            HttpMethod.GET,
            EwpApiConstants.API_BASE_URI
                + EwpApiOutgoingMobilityLearningAgreementCnrV1Controller.BASE_PATH
                + "/"
                + heiId
                + "/stats");

    String responseXml = executeRequest(registryClient, requestBuilder,
        httpSignatureRequestProcessor(registryClient, List.of("test123")))
        .andExpect(status().isOk())
        .andReturn()
        .getResponse()
        .getContentAsString();
    LasIncomingStatsResponseV1 response = XmlUtils.unmarshall(responseXml,
        LasIncomingStatsResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getAcademicYearLaStats()).hasSize(1);
    assertThat(response.getAcademicYearLaStats().get(0).getReceivingAcademicYearId()).isEqualTo(
        receivingAcademicYear);
    assertThat(response.getAcademicYearLaStats().get(0).getLaIncomingTotal()).isEqualTo(
        BigInteger.valueOf(3L));
    assertThat(
        response.getAcademicYearLaStats().get(0).getLaIncomingSomeVersionApproved()).isEqualTo(
        BigInteger.ZERO);
    assertThat(
        response.getAcademicYearLaStats().get(0).getLaIncomingLatestVersionApproved()).isEqualTo(
        BigInteger.ZERO);
    assertThat(
        response.getAcademicYearLaStats().get(0).getLaIncomingLatestVersionRejected()).isEqualTo(
        BigInteger.ZERO);
    assertThat(
        response.getAcademicYearLaStats().get(0).getLaIncomingLatestVersionAwaiting()).isEqualTo(
        BigInteger.ZERO);
  }
}
