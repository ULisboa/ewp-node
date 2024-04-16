package pt.ulisboa.ewp.node.api.ewp.controller.omobilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.OmobilitiesIndexResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.StudentMobilityForStudiesV1;
import eu.erasmuswithoutpaper.api.omobilities.v1.endpoints.StudentMobilityForStudiesV1.SendingHei;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.MockOutgoingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.omobilities.OutgoingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.ounits.OrganizationalUnitsV2HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpOutgoingMobilityMappingRepository;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

class EwpApiOutgoingMobilitiesV1ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

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
        .when(hostPluginManager.getPrimaryProvider(unknownHeiId,
            OrganizationalUnitsV2HostProvider.class))
        .thenReturn(Optional.empty());

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, unknownHeiId);

    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH
            + "/index", queryParams, "Unknown HEI ID: " + unknownHeiId);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOutgoingMobilityIdsRetrieval_ValidSendingHeiIdDividedIntoTwoHosts_AllOmobilityIdsReturned(
      HttpMethod method) throws Exception {
    String sendingHeiId = "test";
    List<String> omobilityIds = Arrays.asList("a1", "b2", "c3");

    MockOutgoingMobilitiesV1HostProvider mockProvider1 = new MockOutgoingMobilitiesV1HostProvider(
        3);
    MockOutgoingMobilitiesV1HostProvider mockProvider2 = new MockOutgoingMobilitiesV1HostProvider(
        3);

    mockProvider1.registerOutgoingMobilityIds(sendingHeiId, List.of(omobilityIds.get(0)));
    mockProvider2.registerOutgoingMobilityIds(sendingHeiId,
        List.of(omobilityIds.get(1), omobilityIds.get(2)));

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(sendingHeiId, OutgoingMobilitiesV1HostProvider.class);
    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(sendingHeiId, OutgoingMobilitiesV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH
                + "/index",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilitiesIndexResponseV1 response = XmlUtils.unmarshall(responseXml,
        OmobilitiesIndexResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getOmobilityId()).isEqualTo(omobilityIds);
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOutgoingMobilitiesGetRetrievalByOmobilityIds_ValidHeiIdDividedIntoTwoHostsWithExistingMappings_AllOutgoingMobilitiesReturned(
      HttpMethod method) throws Exception {
    String sendingHeiId = "test";
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");
    List<String> omobilityIds = Arrays.asList("a1", "b2", "c3");

    List<StudentMobilityForStudiesV1> mobilities = new ArrayList<>();
    for (int index = 0; index < omobilityIds.size(); index++) {
      StudentMobilityForStudiesV1 mobility = new StudentMobilityForStudiesV1();
      mobility.setOmobilityId(omobilityIds.get(index));
      SendingHei sendingHei = new SendingHei();
      sendingHei.setHeiId(sendingHeiId);
      sendingHei.setOunitId(ounitIds.get(index));
      sendingHei.setIiaId(UUID.randomUUID().toString());
      mobility.setSendingHei(sendingHei);
      mobilities.add(mobility);
    }

    MockOutgoingMobilitiesV1HostProvider mockProvider1 = new MockOutgoingMobilitiesV1HostProvider(
        3);
    MockOutgoingMobilitiesV1HostProvider mockProvider2 = new MockOutgoingMobilitiesV1HostProvider(
        3);

    mockProvider1.registerOutgoingMobility(sendingHeiId, omobilityIds.get(0), mobilities.get(0));

    mockProvider2.registerOutgoingMobility(sendingHeiId, omobilityIds.get(1), mobilities.get(1));
    mockProvider2.registerOutgoingMobility(sendingHeiId, omobilityIds.get(2), mobilities.get(2));

    for (int index = 0; index < omobilityIds.size(); index++) {
      doReturn(Optional.of(
          EwpOutgoingMobilityMapping.create(sendingHeiId, ounitIds.get(index),
              omobilityIds.get(index)))).when(mappingRepository)
          .findByHeiIdAndOmobilityId(sendingHeiId, omobilityIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(sendingHeiId, OutgoingMobilitiesV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(sendingHeiId, OutgoingMobilitiesV1HostProvider.class);

    doReturn(Optional.of(mockProvider1)).when(hostPluginManager)
        .getSingleProvider(sendingHeiId, ounitIds.get(0),
            OutgoingMobilitiesV1HostProvider.class);
    doReturn(Optional.of(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(sendingHeiId, ounitIds.get(1),
            OutgoingMobilitiesV1HostProvider.class);
    doReturn(Optional.of(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(sendingHeiId, ounitIds.get(2),
            OutgoingMobilitiesV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilitiesGetResponseV1 response = XmlUtils.unmarshall(responseXml,
        OmobilitiesGetResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getSingleMobilityObject()).hasSize(omobilityIds.size());
    for (StudentMobilityForStudiesV1 mobility : mobilities) {
      assertThat(response.getSingleMobilityObject().stream()
          .map(StudentMobilityForStudiesV1::getOmobilityId)
          .collect(Collectors.toList())).contains(mobility.getOmobilityId());
    }
  }

  @ParameterizedTest
  @EnumSource(value = HttpMethod.class, names = {"GET", "POST"})
  public void testOutgoingMobilitiesGetRetrievalByOmobilityIds_ValidHeiIdDividedIntoTwoHostsWithAllButOneExistingMappings_AllKnownIiasReturned(
      HttpMethod method) throws Exception {
    String sendingHeiId = "test";
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");
    List<String> omobilityIds = Arrays.asList("a1", "b2", "c3");
    List<String> knownOmobilityIds = omobilityIds.subList(0, 2);

    List<StudentMobilityForStudiesV1> mobilities = new ArrayList<>();
    for (int index = 0; index < knownOmobilityIds.size(); index++) {
      StudentMobilityForStudiesV1 mobility = new StudentMobilityForStudiesV1();
      mobility.setOmobilityId(knownOmobilityIds.get(index));
      SendingHei sendingHei = new SendingHei();
      sendingHei.setHeiId(sendingHeiId);
      sendingHei.setOunitId(ounitIds.get(index));
      sendingHei.setIiaId(UUID.randomUUID().toString());
      mobility.setSendingHei(sendingHei);
      mobilities.add(mobility);
    }

    MockOutgoingMobilitiesV1HostProvider mockProvider1 = new MockOutgoingMobilitiesV1HostProvider(
        3);
    MockOutgoingMobilitiesV1HostProvider mockProvider2 = new MockOutgoingMobilitiesV1HostProvider(
        3);

    mockProvider1.registerOutgoingMobility(sendingHeiId, omobilityIds.get(0), mobilities.get(0));

    mockProvider2.registerOutgoingMobility(sendingHeiId, omobilityIds.get(1), mobilities.get(1));

    for (int index = 0; index < omobilityIds.size(); index++) {
      doReturn(Optional.of(
          EwpOutgoingMobilityMapping.create(sendingHeiId, ounitIds.get(index),
              omobilityIds.get(index)))).when(mappingRepository)
          .findByHeiIdAndOmobilityId(sendingHeiId, omobilityIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(sendingHeiId, OutgoingMobilitiesV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(sendingHeiId, OutgoingMobilitiesV1HostProvider.class);

    doReturn(Optional.of(mockProvider1)).when(hostPluginManager)
        .getSingleProvider(sendingHeiId, ounitIds.get(0),
            OutgoingMobilitiesV1HostProvider.class);
    doReturn(Optional.of(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(sendingHeiId, ounitIds.get(1),
            OutgoingMobilitiesV1HostProvider.class);
    doReturn(Optional.of(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(sendingHeiId, ounitIds.get(2),
            OutgoingMobilitiesV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiOutgoingMobilitiesV1Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    OmobilitiesGetResponseV1 response = XmlUtils.unmarshall(responseXml,
        OmobilitiesGetResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getSingleMobilityObject()).hasSize(knownOmobilityIds.size());
    for (StudentMobilityForStudiesV1 mobility : mobilities) {
      assertThat(response.getSingleMobilityObject().stream()
          .map(StudentMobilityForStudiesV1::getOmobilityId)
          .collect(Collectors.toList())).contains(mobility.getOmobilityId());
    }
  }

}