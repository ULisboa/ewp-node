package pt.ulisboa.ewp.node.client.ewp.imobilities.tors;

import eu.erasmuswithoutpaper.api.imobilities.tors.v1.endpoints.ImobilityTorsGetResponseV1;
import eu.erasmuswithoutpaper.api.imobilities.tors.v1.endpoints.ImobilityTorsIndexResponseV1;
import java.time.ZonedDateTime;
import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.tors.ForwardEwpApiIncomingMobilityToRsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilityToRApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.IncomingMobilityToRs;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpIncomingMobilityToRsV1Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpIncomingMobilityToRsV1Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public ForwardEwpApiIncomingMobilityToRsApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpIncomingMobilityToRApiConfiguration apiConfiguration = getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiIncomingMobilityToRsApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<ImobilityTorsIndexResponseV1> findOutgoingMobilityIdsWithTranscriptOfRecord(
      String receivingHeiId, Collection<String> sendingHeiIds, ZonedDateTime modifiedSince)
      throws EwpClientErrorException {
    EwpIncomingMobilityToRApiConfiguration api = getApiConfigurationForHeiId(
        receivingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiIds);
    bodyParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);

    EwpRequest request = EwpRequest.createPost(api, "index", api.getIndexUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(request, ImobilityTorsIndexResponseV1.class);
  }

  public EwpSuccessOperationResult<ImobilityTorsGetResponseV1> findByReceivingHeiIdAndOutgoingMobilityIds(
      String receivingHeiId, Collection<String> outgoingMobilityIds)
      throws EwpClientErrorException {
    EwpIncomingMobilityToRApiConfiguration api = getApiConfigurationForHeiId(
        receivingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    EwpRequest request = EwpRequest.createPost(api, "get", api.getGetUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(request, ImobilityTorsGetResponseV1.class);
  }

  protected EwpIncomingMobilityToRApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return IncomingMobilityToRs.V1.getConfigurationForHeiId(registryClient, heiId);
  }
}
