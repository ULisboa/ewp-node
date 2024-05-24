package pt.ulisboa.ewp.node.client.ewp.factsheet;

import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetResponseV1;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient.ResponseBodySpecification;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpFactsheetApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.FactSheets;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpFactsheetsV1Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpFactsheetsV1Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public EwpSuccessOperationResult<FactsheetResponseV1> findByHeiId(String heiId)
      throws EwpClientErrorException {
    EwpFactsheetApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);

    EwpRequest request = EwpRequest.createPost(api, "", api.getUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(
        request, ResponseBodySpecification.createStrict(FactsheetResponseV1.class));
  }

  protected EwpFactsheetApiConfiguration getApiConfigurationForHeiId(String heiId) {
    return FactSheets.V1.getConfigurationForHeiId(registryClient, heiId);
  }
}
