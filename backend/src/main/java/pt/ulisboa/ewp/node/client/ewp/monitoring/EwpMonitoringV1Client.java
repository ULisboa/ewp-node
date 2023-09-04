package pt.ulisboa.ewp.node.client.ewp.monitoring;

import eu.erasmuswithoutpaper.api.monitoring.v1.MonitoringResponseV1;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpMonitoringApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.Monitoring;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpMonitoringV1Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;
  private final String monitoringHeiId;

  public EwpMonitoringV1Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient, @Value("${stats.portal.heiId}") String monitoringHeiId) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
    this.monitoringHeiId = monitoringHeiId;
  }

  public EwpSuccessOperationResult<MonitoringResponseV1> reportIssue(String serverHeiId, String apiName,
      String endpointName, Integer httpCode, String serverMessage, String clientMessage)
      throws EwpClientErrorException {
    EwpMonitoringApiConfiguration api = getApiConfigurationForHeiId(monitoringHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.SERVER_HEI_ID, serverHeiId);
    bodyParams.param(EwpApiParamConstants.API_NAME, apiName);
    bodyParams.param(EwpApiParamConstants.ENDPOINT_NAME, endpointName);
    bodyParams.param(EwpApiParamConstants.HTTP_CODE, httpCode);
    bodyParams.param(EwpApiParamConstants.SERVER_MESSAGE, serverMessage);
    bodyParams.param(EwpApiParamConstants.CLIENT_MESSAGE, clientMessage);

    EwpRequest request =
        EwpRequest.createPost(api, "", api.getUrl(), new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(request, MonitoringResponseV1.class);
  }

  protected EwpMonitoringApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return Monitoring.V1.getConfigurationForHeiId(registryClient, heiId);
  }
}
