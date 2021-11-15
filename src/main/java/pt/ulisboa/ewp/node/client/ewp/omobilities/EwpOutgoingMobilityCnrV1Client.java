package pt.ulisboa.ewp.node.client.ewp.omobilities;

import eu.erasmuswithoutpaper.api.omobilities.cnr.v1.OmobilityCnrResponseV1;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.cnr.ForwardEwpApiOutgoingMobilityCnrApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilityCnrApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.OutgoingMobilityCnr;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpOutgoingMobilityCnrV1Client
    extends EwpApiClient<EwpOutgoingMobilityCnrApiConfiguration> {

  public EwpOutgoingMobilityCnrV1Client(RegistryClient registryClient,
      EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public ForwardEwpApiOutgoingMobilityCnrApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpOutgoingMobilityCnrApiConfiguration apiConfiguration = getApiConfigurationForHeiId(
        heiId);
    return new ForwardEwpApiOutgoingMobilityCnrApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<OmobilityCnrResponseV1> sendChangeNotification(
      String sendingHeiId, String receivingHeiId, List<String> outgoingMobilityIds)
      throws EwpClientErrorException {
    EwpOutgoingMobilityCnrApiConfiguration api = getApiConfigurationForHeiId(
        receivingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    EwpRequest request = EwpRequest.createPost(api, api.getUrl(),
        new EwpRequestFormDataBody(bodyParams));
    return ewpClient.executeAndLog(request, OmobilityCnrResponseV1.class);
  }

  @Override
  public EwpApiVersionSpecification<?, EwpOutgoingMobilityCnrApiConfiguration>
  getApiVersionSpecification() {
    return OutgoingMobilityCnr.V1;
  }
}
