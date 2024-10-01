package pt.ulisboa.ewp.node.api.ewp.controller.iias.cnr;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import eu.erasmuswithoutpaper.api.iias.cnr.v3.IiaCnrResponseV3;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia;
import eu.erasmuswithoutpaper.api.iias.v7.endpoints.IiasGetResponseV7.Iia.Partner;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpMethod;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.cnr.InterInstitutionalAgreementCnrV3HostProvider;
import pt.ulisboa.ewp.host.plugin.skeleton.provider.iias.cnr.MockInterInstitutionalAgreementsCnrV3HostProvider;
import pt.ulisboa.ewp.node.api.ewp.AbstractEwpControllerIntegrationTest;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.iias.EwpInterInstitutionalAgreementsV7Client;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult.Builder;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.plugin.manager.host.HostPluginManager;
import pt.ulisboa.ewp.node.utils.http.HttpParams;
import pt.ulisboa.ewp.node.utils.tests.provider.argument.HttpGetAndPostArgumentProvider;
import pt.ulisboa.ewp.node.utils.xml.XmlUtils;

class EwpApiInterInstitutionalAgreementsCnrV3ControllerIntegrationTest
    extends AbstractEwpControllerIntegrationTest {

  @Autowired private HostPluginManager hostPluginManager;

  @MockBean private RegistryClient registryClient;

  @MockBean private EwpInterInstitutionalAgreementsV7Client iiaClient;

  @ParameterizedTest
  @ArgumentsSource(HttpGetAndPostArgumentProvider.class)
  public void testIiaCnr_ExistingIiaAndTwoHostProviders_CorrectHostProviderInvoked(
      HttpMethod method) throws Exception {
    String notifierHeiId = "notifierHeiId";
    String iiaId = "iiaId1";
    String localHeiId = "sample.edu";
    String localOunitId = "localOunitId1";

    Iia iia = new Iia();
    Partner partner1 = new Partner();
    partner1.setHeiId(notifierHeiId);
    partner1.setIiaId(iiaId);
    iia.getPartner().add(partner1);
    Partner partner2 = new Partner();
    partner2.setHeiId(localHeiId);
    partner2.setOunitId(localOunitId);
    iia.getPartner().add(partner2);
    IiasGetResponseV7 iiasGetResponse = new IiasGetResponseV7();
    iiasGetResponse.getIia().add(iia);
    EwpSuccessOperationResult<IiasGetResponseV7> iiaGetSuccessOperationResult =
        new Builder<IiasGetResponseV7>().responseBody(iiasGetResponse).build();
    doReturn(iiaGetSuccessOperationResult)
        .when(iiaClient)
        .findByHeiIdAndIiaIds(notifierHeiId, List.of(iiaId));

    MockInterInstitutionalAgreementsCnrV3HostProvider mockIiaCnrProvider1 =
        Mockito.spy(new MockInterInstitutionalAgreementsCnrV3HostProvider());
    MockInterInstitutionalAgreementsCnrV3HostProvider mockIiaCnrProvider2 =
        Mockito.spy(new MockInterInstitutionalAgreementsCnrV3HostProvider());

    doReturn(Optional.of(mockIiaCnrProvider1))
        .when(hostPluginManager)
        .getSingleProvider(
            localHeiId, localOunitId, InterInstitutionalAgreementCnrV3HostProvider.class);

    HttpParams queryParams = new HttpParams();
    queryParams.param(EwpApiParamConstants.IIA_ID, iiaId);

    String responseXml =
        executeRequest(
                registryClient,
                method,
                EwpApiConstants.API_BASE_URI
                    + EwpApiInterInstitutionalAgreementsCnrV3Controller.BASE_PATH
                    + "/"
                    + localHeiId,
                queryParams,
                notifierHeiId)
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
    IiaCnrResponseV3 response = XmlUtils.unmarshall(responseXml, IiaCnrResponseV3.class);

    assertThat(response).isNotNull();

    verify(mockIiaCnrProvider1, times(1)).onChangeNotification(notifierHeiId, iiaId);
    verify(mockIiaCnrProvider2, times(0)).onChangeNotification(notifierHeiId, iiaId);
  }
}
