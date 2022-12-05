package pt.ulisboa.ewp.node.client.ewp.imobilities.tors;

import eu.erasmuswithoutpaper.api.imobilities.tors.v2.endpoints.ImobilityTorsGetResponseV2;
import eu.erasmuswithoutpaper.api.imobilities.tors.v2.endpoints.ImobilityTorsIndexResponseV2;
import java.time.ZonedDateTime;
import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.tors.ForwardEwpApiIncomingMobilityToRsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilityToRApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.IncomingMobilityToRs;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpIncomingMobilityToRsV2Client extends
    EwpApiClient<EwpIncomingMobilityToRApiConfiguration> {

  public EwpIncomingMobilityToRsV2Client(RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public ForwardEwpApiIncomingMobilityToRsApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpIncomingMobilityToRApiConfiguration apiConfiguration = getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiIncomingMobilityToRsApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<ImobilityTorsIndexResponseV2> findOutgoingMobilityIdsWithTranscriptOfRecord(
      String receivingHeiId, Collection<String> sendingHeiIds, ZonedDateTime modifiedSince)
      throws EwpClientErrorException {
    EwpIncomingMobilityToRApiConfiguration api = getApiConfigurationForHeiId(
        receivingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiIds);
    bodyParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);

    EwpRequest request = EwpRequest.createPost(api, api.getIndexUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpClient.executeAndLog(request, ImobilityTorsIndexResponseV2.class);
  }

  public EwpSuccessOperationResult<ImobilityTorsGetResponseV2> findByReceivingHeiIdAndOutgoingMobilityIds(
      String receivingHeiId, Collection<String> outgoingMobilityIds)
      throws EwpClientErrorException {
    EwpIncomingMobilityToRApiConfiguration api = getApiConfigurationForHeiId(
        receivingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    EwpRequest request = EwpRequest.createPost(api, api.getGetUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpClient.executeAndLog(request, ImobilityTorsGetResponseV2.class);
  }

  @Override
  public EwpApiVersionSpecification<?, EwpIncomingMobilityToRApiConfiguration> getApiVersionSpecification() {
    return IncomingMobilityToRs.V2;
  }
}
