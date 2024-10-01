package pt.ulisboa.ewp.node.api.ewp.controller.courses;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.courses.v0.CoursesResponseV0;
import eu.erasmuswithoutpaper.api.courses.v0.CoursesResponseV0.LearningOpportunitySpecification;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.EnumSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.CoursesV0HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.courses.MockCoursesV0HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.tests.provider.argument.HttpGetAndPostArgumentProvider;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

class EwpApiCoursesV0ControllerTest extends AbstractEwpControllerIntegrationTest {

  @Autowired
  private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  public void testLearningOpportunitySpecificationsRetrieval_UnknownHeiId_ErrorReturned(
      HttpMethod method) throws Exception {
    String heiId = "test";

    doReturn(false).when(hostPluginManager)
        .hasHostProvider(heiId, CoursesV0HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);

    assertBadRequest(registryClient, method,
        EwpApiConstants.API_BASE_URI + EwpApiCoursesV0Controller.BASE_PATH,
        queryParams,
        "Unknown HEI ID: " + heiId);
  }

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  public void
      testLearningOpportunitySpecificationsRetrieval_ValidHeiIdAndThreeValidAndOneUnknownLosIdsDividedIntoTwoHosts_AllKnownLosReturned(
          HttpMethod method) throws Exception {
    Mockito.reset(this.hostPluginManager);

    String heiId = "test";
    List<String> losIds = Arrays.asList("a1", "b2", "c3", "d4");
    List<String> knownLosIds = Arrays.asList("a1", "b2", "d4");

    MockCoursesV0HostProvider provider1 = new MockCoursesV0HostProvider(5,
        5);
    LearningOpportunitySpecification learningOpportunitySpecificationA1 = createDummyLearningOpportunitySpecification(
        losIds.get(0), null);
    LearningOpportunitySpecification learningOpportunitySpecificationB2 = createDummyLearningOpportunitySpecification(
        losIds.get(1), null);
    provider1.registerLearningOpportunitySpecification(heiId,
        learningOpportunitySpecificationA1);
    provider1.registerLearningOpportunitySpecification(heiId,
        learningOpportunitySpecificationB2);

    MockCoursesV0HostProvider provider2 = new MockCoursesV0HostProvider(5,
        5);
    LearningOpportunitySpecification learningOpportunitySpecificationD4 = createDummyLearningOpportunitySpecification(
        losIds.get(3), null);
    provider2.registerLearningOpportunitySpecification(heiId,
        learningOpportunitySpecificationA1);
    provider2.registerLearningOpportunitySpecification(heiId,
        learningOpportunitySpecificationD4);

    doReturn(List.of(provider1, provider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, CoursesV0HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.LOS_ID, losIds);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiCoursesV0Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    CoursesResponseV0 response = XmlUtils.unmarshall(responseXml, CoursesResponseV0.class);

    assertThat(response).isNotNull();
    assertThat(response.getLearningOpportunitySpecification().stream().map(
        LearningOpportunitySpecification::getLosId).distinct().collect(
        Collectors.toList())).hasSize(knownLosIds.size());
    for (LearningOpportunitySpecification returnedLearningOpportunitySpecification : response.getLearningOpportunitySpecification()) {
      assertThat(knownLosIds).contains(returnedLearningOpportunitySpecification.getLosId());
    }
  }

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  public void
      testLearningOpportunitySpecificationsRetrieval_ValidHeiIdAndThreeValidAndOneUnknownLosCodesDividedIntoTwoHosts_AllKnownLosReturned(
          HttpMethod method) throws Exception {
    Mockito.reset(this.hostPluginManager);
    
    String heiId = "test";
    List<String> losCodes = Arrays.asList("a1", "b2", "c3", "d4");
    List<String> knownLosCodes = Arrays.asList("a1", "b2", "d4");

    MockCoursesV0HostProvider provider1 = new MockCoursesV0HostProvider(5,
        5);
    LearningOpportunitySpecification learningOpportunitySpecificationA1 = createDummyLearningOpportunitySpecification(
        null, losCodes.get(0));
    LearningOpportunitySpecification learningOpportunitySpecificationB2 = createDummyLearningOpportunitySpecification(
        null, losCodes.get(1));
    provider1.registerLearningOpportunitySpecification(heiId,
        learningOpportunitySpecificationA1);
    provider1.registerLearningOpportunitySpecification(heiId,
        learningOpportunitySpecificationB2);

    MockCoursesV0HostProvider provider2 = new MockCoursesV0HostProvider(5,
        5);
    LearningOpportunitySpecification learningOpportunitySpecificationD4 = createDummyLearningOpportunitySpecification(
        null, losCodes.get(3));
    provider2.registerLearningOpportunitySpecification(heiId,
        learningOpportunitySpecificationA1);
    provider2.registerLearningOpportunitySpecification(heiId,
        learningOpportunitySpecificationD4);

    doReturn(List.of(provider1, provider2)).when(hostPluginManager)
        .getAllProvidersOfType(heiId, CoursesV0HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.HEI_ID, heiId);
    queryParams.param(EwpApiParamConstants.LOS_CODE, losCodes);

    String responseXml =
        executeRequest(registryClient, method,
            EwpApiConstants.API_BASE_URI + EwpApiCoursesV0Controller.BASE_PATH,
            queryParams)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    CoursesResponseV0 response = XmlUtils.unmarshall(responseXml, CoursesResponseV0.class);

    assertThat(response).isNotNull();
    assertThat(response.getLearningOpportunitySpecification().stream().map(
        LearningOpportunitySpecification::getLosCode).distinct().collect(
        Collectors.toList())).hasSize(knownLosCodes.size());
    for (LearningOpportunitySpecification returnedLearningOpportunitySpecification : response.getLearningOpportunitySpecification()) {
      assertThat(knownLosCodes).contains(returnedLearningOpportunitySpecification.getLosCode());
    }
  }

  private static LearningOpportunitySpecification createDummyLearningOpportunitySpecification(
      String losId, String losCode) {
    LearningOpportunitySpecification learningOpportunitySpecification = new LearningOpportunitySpecification();
    learningOpportunitySpecification.setLosId(losId);
    learningOpportunitySpecification.setLosCode(losCode);
    learningOpportunitySpecification.setIscedCode("6");
    return learningOpportunitySpecification;
  }

}