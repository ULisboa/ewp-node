package pt.ulisboa.ewp.node.api.ewp.controller.omobilities.stats;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.omobilities.stats.v1.OmobilityStatsResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.stats.v1.OmobilityStatsResponseV1.AcademicYearStats;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import org.assertj.core.api.Condition;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.stats.MockOutgoingMobilityStatsV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.stats.OutgoingMobilityStatsV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

@TestPropertySource(
    properties = {
      "stats.portal.heiId=test123",
    })
class EwpApiOutgoingMobilityStatsV1ControllerIntegrationTest
    extends AbstractEwpControllerIntegrationTest {

  @Autowired private HostPluginManager hostPluginManager;

  @MockitoBean
  private RegistryClient registryClient;

  @Test
  public void testStatsRetrieval_InvalidSendingHeiId_ErrorReturned() throws Exception {
    String invalidSendingHeiId = UUID.randomUUID().toString();

    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.request(
            HttpMethod.GET,
            EwpApiConstants.API_BASE_URI
                + EwpApiOutgoingMobilityStatsV1Controller.BASE_PATH
                + "/"
                + invalidSendingHeiId);

    assertErrorRequest(
        requestBuilder,
        httpSignatureRequestProcessor(registryClient, List.of("wrong-stats-portal-hei-id")),
        HttpStatus.BAD_REQUEST,
        new Condition<>(
            errorResponse ->
                errorResponse.getDeveloperMessage().getValue().startsWith("Unauthorized HEI ID"),
            "valid developer message"));
  }

  @Test
  public void testStatsRetrieval_ValidRequesterHeiIdAndUnknownSendingHeiId_EmptyStatsReturned()
      throws Exception {
    String unknownSendingHeiId = UUID.randomUUID().toString();

    doReturn(List.of())
        .when(hostPluginManager)
        .getAllProvidersOfType(unknownSendingHeiId, OutgoingMobilityStatsV1HostProvider.class);

    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.request(
            HttpMethod.GET,
            EwpApiConstants.API_BASE_URI
                + EwpApiOutgoingMobilityStatsV1Controller.BASE_PATH
                + "/"
                + unknownSendingHeiId);

    String responseXml =
        executeRequest(
                registryClient,
                requestBuilder,
                httpSignatureRequestProcessor(registryClient, List.of("test123")))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilityStatsResponseV1 response =
        XmlUtils.unmarshall(responseXml, OmobilityStatsResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getAcademicYearStats()).isEmpty();
  }

  @Test
  public void testStatsRetrieval_ValidRequesterHeiIdAndTwoHostProviders_StatsMergedReturned()
      throws Exception {
    String sendingHeiId = UUID.randomUUID().toString();
    String receivingAcademicYear = "0000/0001";

    MockOutgoingMobilityStatsV1HostProvider mockProvider1 =
        new MockOutgoingMobilityStatsV1HostProvider();

    OmobilityStatsResponseV1 stats1 = new OmobilityStatsResponseV1();
    AcademicYearStats academicYearStats1 = new AcademicYearStats();
    academicYearStats1.setReceivingAcademicYearId(receivingAcademicYear);
    academicYearStats1.setOmobilityApproved(BigInteger.ONE);
    academicYearStats1.setOmobilityPending(BigInteger.TWO);
    stats1.getAcademicYearStats().add(academicYearStats1);

    mockProvider1.registerStats(sendingHeiId, stats1);

    MockOutgoingMobilityStatsV1HostProvider mockProvider2 =
        new MockOutgoingMobilityStatsV1HostProvider();

    OmobilityStatsResponseV1 stats2 = new OmobilityStatsResponseV1();
    AcademicYearStats academicYearStats2 = new AcademicYearStats();
    academicYearStats2.setReceivingAcademicYearId(receivingAcademicYear);
    academicYearStats2.setOmobilityApproved(BigInteger.TEN);
    stats2.getAcademicYearStats().add(academicYearStats2);

    mockProvider2.registerStats(sendingHeiId, stats2);

    doReturn(Arrays.asList(mockProvider1, mockProvider2))
        .when(hostPluginManager)
        .getAllProvidersOfType(sendingHeiId, OutgoingMobilityStatsV1HostProvider.class);

    MockHttpServletRequestBuilder requestBuilder =
        MockMvcRequestBuilders.request(
            HttpMethod.GET,
            EwpApiConstants.API_BASE_URI
                + EwpApiOutgoingMobilityStatsV1Controller.BASE_PATH
                + "/"
                + sendingHeiId);

    String responseXml =
        executeRequest(
                registryClient,
                requestBuilder,
                httpSignatureRequestProcessor(registryClient, List.of("test123")))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilityStatsResponseV1 response =
        XmlUtils.unmarshall(responseXml, OmobilityStatsResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getAcademicYearStats()).hasSize(1);
    assertThat(response.getAcademicYearStats().get(0).getReceivingAcademicYearId())
        .isEqualTo(receivingAcademicYear);
    assertThat(response.getAcademicYearStats().get(0).getOmobilityApproved())
        .isEqualTo(BigInteger.valueOf(11L));
    assertThat(response.getAcademicYearStats().get(0).getOmobilityPending())
        .isEqualTo(BigInteger.valueOf(2L));
  }
}
