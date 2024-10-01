package pt.ulisboa.ewp.node.api.ewp.controller.imobilities;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.imobilities.v1.endpoints.ImobilitiesGetResponseV1;
import eu.erasmuswithoutpaper.api.imobilities.v1.endpoints.StudentMobilityForStudiesV1;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.IncomingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.imobilities.MockIncomingMobilitiesV1HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.mapping.EwpOutgoingMobilityMapping;
import pt.ulisboa.ewp.node.domain.repository.mapping.EwpOutgoingMobilityMappingRepository;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.tests.provider.argument.HttpGetAndPostArgumentProvider;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

class EwpApiIncomingMobilitiesV1ControllerIntegrationTest extends
    AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @SpyBean
  private EwpOutgoingMobilityMappingRepository mappingRepository;

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  public void
      testIncomingMobilitiesGetRetrievalByOmobilityIds_ValidHeiIdDividedIntoTwoHostsWithExistingMappings_AllIncomingMobilitiesReturned(
          HttpMethod method) throws Exception {
    String receivingHeiId = "test";
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");
    List<String> omobilityIds = Arrays.asList("a1", "b2", "c3");

    List<StudentMobilityForStudiesV1> mobilities = new ArrayList<>();
    for (int index = 0; index < omobilityIds.size(); index++) {
      StudentMobilityForStudiesV1 mobility = new StudentMobilityForStudiesV1();
      mobility.setOmobilityId(omobilityIds.get(index));
      mobility.setComment("TEST");
      mobilities.add(mobility);
    }

    MockIncomingMobilitiesV1HostProvider mockProvider1 = new MockIncomingMobilitiesV1HostProvider(
        3);
    MockIncomingMobilitiesV1HostProvider mockProvider2 = new MockIncomingMobilitiesV1HostProvider(
        3);

    mockProvider1.registerIncomingMobility(receivingHeiId, omobilityIds.get(0), mobilities.get(0));

    mockProvider2.registerIncomingMobility(receivingHeiId, omobilityIds.get(1), mobilities.get(1));
    mockProvider2.registerIncomingMobility(receivingHeiId, omobilityIds.get(2), mobilities.get(2));

    for (int index = 0; index < omobilityIds.size(); index++) {
      doReturn(Optional.of(
          EwpOutgoingMobilityMapping.create(receivingHeiId, ounitIds.get(index),
              omobilityIds.get(index)))).when(mappingRepository)
          .findByHeiIdAndOmobilityId(receivingHeiId, omobilityIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(receivingHeiId, IncomingMobilitiesV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(receivingHeiId, IncomingMobilitiesV1HostProvider.class);

    doReturn(Optional.of(mockProvider1)).when(hostPluginManager)
        .getSingleProvider(receivingHeiId, ounitIds.get(0),
            IncomingMobilitiesV1HostProvider.class);
    doReturn(Optional.of(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(receivingHeiId, ounitIds.get(1),
            IncomingMobilitiesV1HostProvider.class);
    doReturn(Optional.of(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(receivingHeiId, ounitIds.get(2),
            IncomingMobilitiesV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiIncomingMobilitiesV1Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    ImobilitiesGetResponseV1 response = XmlUtils.unmarshall(responseXml,
        ImobilitiesGetResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getSingleIncomingMobilityObject()).hasSize(omobilityIds.size());
    for (StudentMobilityForStudiesV1 mobility : mobilities) {
      assertThat(response.getSingleIncomingMobilityObject().stream()
          .map(StudentMobilityForStudiesV1::getOmobilityId)
          .collect(Collectors.toList())).contains(mobility.getOmobilityId());
    }
  }

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  public void testIncomingMobilitiesGetRetrievalByOmobilityIds_ValidHeiIdDividedIntoTwoHostsWithAllButOneExistingMappings_AllKnownIiasReturned(
      HttpMethod method) throws Exception {
    String receivingHeiId = "test";
    List<String> ounitIds = Arrays.asList("o1", "o2", "o3");
    List<String> omobilityIds = Arrays.asList("a1", "b2", "c3");
    List<String> knownOmobilityIds = omobilityIds.subList(0, 2);

    List<StudentMobilityForStudiesV1> mobilities = new ArrayList<>();
    for (int index = 0; index < knownOmobilityIds.size(); index++) {
      StudentMobilityForStudiesV1 mobility = new StudentMobilityForStudiesV1();
      mobility.setOmobilityId(knownOmobilityIds.get(index));
      mobility.setComment("TEST");
      mobilities.add(mobility);
    }

    MockIncomingMobilitiesV1HostProvider mockProvider1 = new MockIncomingMobilitiesV1HostProvider(
        3);
    MockIncomingMobilitiesV1HostProvider mockProvider2 = new MockIncomingMobilitiesV1HostProvider(
        3);

    mockProvider1.registerIncomingMobility(receivingHeiId, omobilityIds.get(0), mobilities.get(0));

    mockProvider2.registerIncomingMobility(receivingHeiId, omobilityIds.get(1), mobilities.get(1));

    for (int index = 0; index < omobilityIds.size(); index++) {
      doReturn(Optional.of(
          EwpOutgoingMobilityMapping.create(receivingHeiId, ounitIds.get(index),
              omobilityIds.get(index)))).when(mappingRepository)
          .findByHeiIdAndOmobilityId(receivingHeiId, omobilityIds.get(index));
    }

    doReturn(true).when(hostPluginManager)
        .hasHostProvider(receivingHeiId, IncomingMobilitiesV1HostProvider.class);

    doReturn(Arrays.asList(mockProvider1, mockProvider2)).when(hostPluginManager)
        .getAllProvidersOfType(receivingHeiId, IncomingMobilitiesV1HostProvider.class);

    doReturn(Optional.of(mockProvider1)).when(hostPluginManager)
        .getSingleProvider(receivingHeiId, ounitIds.get(0),
            IncomingMobilitiesV1HostProvider.class);
    doReturn(Optional.of(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(receivingHeiId, ounitIds.get(1),
            IncomingMobilitiesV1HostProvider.class);
    doReturn(Optional.of(mockProvider2)).when(hostPluginManager)
        .getSingleProvider(receivingHeiId, ounitIds.get(2),
            IncomingMobilitiesV1HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    queryParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiIncomingMobilitiesV1Controller.BASE_PATH
                + "/get",
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    ImobilitiesGetResponseV1 response = XmlUtils.unmarshall(responseXml,
        ImobilitiesGetResponseV1.class);

    assertThat(response).isNotNull();
    assertThat(response.getSingleIncomingMobilityObject()).hasSize(knownOmobilityIds.size());
    for (StudentMobilityForStudiesV1 mobility : mobilities) {
      assertThat(response.getSingleIncomingMobilityObject().stream()
          .map(StudentMobilityForStudiesV1::getOmobilityId)
          .collect(Collectors.toList())).contains(mobility.getOmobilityId());
    }
  }

}