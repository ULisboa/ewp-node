package pt.ulisboa.ewp.node.client.ewp.omobilities.cnr;

import eu.erasmuswithoutpaper.api.omobilities.cnr.v2.OmobilityCnrResponseV2;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.cnr.ForwardEwpApiOutgoingMobilityCnrApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient.ResponseBodySpecification;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilityCnrApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.OutgoingMobilityCnr;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpOutgoingMobilityCnrV2Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpOutgoingMobilityCnrV2Client(
      RegistryClient registryClient, EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public ForwardEwpApiOutgoingMobilityCnrApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpOutgoingMobilityCnrApiConfiguration apiConfiguration = getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiOutgoingMobilityCnrApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<OmobilityCnrResponseV2> sendChangeNotification(
      String receivingHeiId, List<String> outgoingMobilityIds) throws EwpClientErrorException {
    EwpOutgoingMobilityCnrApiConfiguration api = getApiConfigurationForHeiId(receivingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    EwpRequest request =
        EwpRequest.createPost(
            api, "", api.getUrl(), new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(
        request, ResponseBodySpecification.createWithOptionalType(OmobilityCnrResponseV2.class));
  }

  protected EwpOutgoingMobilityCnrApiConfiguration getApiConfigurationForHeiId(String heiId) {
    return OutgoingMobilityCnr.V2.getConfigurationForHeiId(registryClient, heiId);
  }
}
