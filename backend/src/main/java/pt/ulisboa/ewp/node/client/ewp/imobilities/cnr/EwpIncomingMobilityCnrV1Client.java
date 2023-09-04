package pt.ulisboa.ewp.node.client.ewp.imobilities.cnr;

import eu.erasmuswithoutpaper.api.imobilities.cnr.v1.ImobilityCnrResponseV1;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilityCnrApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.IncomingMobilityCnr;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpIncomingMobilityCnrV1Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpIncomingMobilityCnrV1Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public EwpSuccessOperationResult<ImobilityCnrResponseV1> sendChangeNotification(
      String sendingHeiId, String receivingHeiId, List<String> outgoingMobilityIds)
      throws EwpClientErrorException {
    EwpIncomingMobilityCnrApiConfiguration api = getApiConfigurationForHeiId(
        sendingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    EwpRequest request = EwpRequest.createPost(api, "", api.getUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(request, ImobilityCnrResponseV1.class);
  }

  protected EwpIncomingMobilityCnrApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return IncomingMobilityCnr.V1.getConfigurationForHeiId(registryClient, heiId);
  }
}
