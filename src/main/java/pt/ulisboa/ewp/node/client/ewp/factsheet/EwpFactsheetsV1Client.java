package pt.ulisboa.ewp.node.client.ewp.factsheet;

import eu.erasmuswithoutpaper.api.factsheet.v1.FactsheetResponseV1;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpFactsheetApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications.EwpApiGeneralSpecification;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpFactsheetsV1Client extends
    EwpApiClient<EwpFactsheetApiConfiguration> {

  public EwpFactsheetsV1Client(RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public EwpSuccessOperationResult<FactsheetResponseV1> findByHeiId(String heiId)
      throws EwpClientErrorException {
    EwpFactsheetApiConfiguration api = getApiConfigurationForHeiId(heiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.HEI_ID, heiId);

    EwpRequest request = EwpRequest.createPost(api, api.getUrl(), bodyParams);
    return ewpClient.executeAndLog(request, FactsheetResponseV1.class);
  }

  @Override
  public EwpApiGeneralSpecification<?, EwpFactsheetApiConfiguration> getApiGeneralSpecification() {
    return EwpApiGeneralSpecifications.FACTSHEETS_V1;
  }
}
