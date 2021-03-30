package pt.ulisboa.ewp.node.client.ewp.imobilities;

import eu.erasmuswithoutpaper.api.imobilities.v1.endpoints.ImobilitiesGetResponseV1;
import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilitiesApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications;
import pt.ulisboa.ewp.node.utils.EwpApiGeneralSpecifications.EwpApiGeneralSpecification;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpIncomingMobilitiesV1Client extends
    EwpApiClient<EwpIncomingMobilitiesApiConfiguration> {

  public EwpIncomingMobilitiesV1Client(RegistryClient registryClient, EwpClient ewpClient) {
    super(registryClient, ewpClient);
  }

  public ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpIncomingMobilitiesApiConfiguration apiConfiguration = getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<ImobilitiesGetResponseV1> findByReceivingHeiIdAndOmobilityIds(
      String receivingHeiId, Collection<String> omobilityIds)
      throws AbstractEwpClientErrorException {
    EwpIncomingMobilitiesApiConfiguration api = getApiConfigurationForHeiId(
        receivingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    EwpRequest request = EwpRequest.createPost(api, api.getGetUrl(), bodyParams);
    return ewpClient.executeWithLoggingExpectingSuccess(request, ImobilitiesGetResponseV1.class);
  }

  @Override
  public EwpApiGeneralSpecification<?, EwpIncomingMobilitiesApiConfiguration> getApiGeneralSpecification() {
    return EwpApiGeneralSpecifications.INCOMING_MOBILITIES_V1;
  }
}
