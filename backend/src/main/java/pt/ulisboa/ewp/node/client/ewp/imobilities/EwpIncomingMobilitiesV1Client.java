package pt.ulisboa.ewp.node.client.ewp.imobilities;

import eu.erasmuswithoutpaper.api.imobilities.v1.endpoints.ImobilitiesGetResponseV1;
import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilitiesApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.IncomingMobilities;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpIncomingMobilitiesV1Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpIncomingMobilitiesV1Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpIncomingMobilitiesApiConfiguration apiConfiguration = getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<ImobilitiesGetResponseV1> findByReceivingHeiIdAndOmobilityIds(
      String receivingHeiId, Collection<String> omobilityIds)
      throws EwpClientErrorException {
    EwpIncomingMobilitiesApiConfiguration api = getApiConfigurationForHeiId(
        receivingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    EwpRequest request = EwpRequest.createPost(api, api.getGetUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(request, ImobilitiesGetResponseV1.class);
  }

  protected EwpIncomingMobilitiesApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return IncomingMobilities.V1.getConfigurationForHeiId(registryClient, heiId);
  }
}
