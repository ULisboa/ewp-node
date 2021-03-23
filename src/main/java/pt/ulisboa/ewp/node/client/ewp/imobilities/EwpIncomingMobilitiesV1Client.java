package pt.ulisboa.ewp.node.client.ewp.imobilities;

import eu.erasmuswithoutpaper.api.imobilities.v1.ImobilitiesV1;
import eu.erasmuswithoutpaper.api.imobilities.v1.endpoints.ImobilitiesGetResponseV1;
import java.util.Collection;
import java.util.Optional;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiUtils;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.imobilities.ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.AbstractEwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.exception.NoEwpApiForHeiIdAndMajorVersionException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.result.success.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpIncomingMobilitiesApiConfiguration;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpIncomingMobilitiesV1Client {

  private static final int API_MAJOR_VERSION = 1;

  private final RegistryClient registryClient;
  private final EwpClient ewpClient;

  public EwpIncomingMobilitiesV1Client(RegistryClient registryClient, EwpClient ewpClient) {
    this.registryClient = registryClient;
    this.ewpClient = ewpClient;
  }

  public ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpIncomingMobilitiesApiConfiguration apiConfiguration = getApiConfiguration(heiId);
    return new ForwardEwpApiIncomingMobilitiesApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<ImobilitiesGetResponseV1> findByReceivingHeiIdAndOmobilityIds(
      String receivingHeiId, Collection<String> omobilityIds)
      throws AbstractEwpClientErrorException {
    EwpIncomingMobilitiesApiConfiguration apiConfiguration = getApiConfiguration(receivingHeiId);

    EwpRequest request = new EwpRequest(HttpMethod.POST, apiConfiguration.getGetUrl());
    request.authenticationMethod(
        EwpApiUtils.getBestSupportedApiAuthenticationMethod(apiConfiguration));

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);
    request.bodyParams(bodyParams);

    return ewpClient.executeWithLoggingExpectingSuccess(request, ImobilitiesGetResponseV1.class);
  }

  public EwpIncomingMobilitiesApiConfiguration getApiConfiguration(String heiId) {
    Optional<ImobilitiesV1> apiElementOptional =
        EwpApiUtils.getApiElement(
            registryClient,
            heiId,
            EwpApiConstants.API_INCOMING_MOBILITIES_NAME,
            API_MAJOR_VERSION,
            ImobilitiesV1.class);
    if (apiElementOptional.isEmpty()) {
      throw new NoEwpApiForHeiIdAndMajorVersionException(
          heiId, EwpIncomingMobilitiesApiConfiguration.API_NAME, API_MAJOR_VERSION);
    }
    ImobilitiesV1 apiElement = apiElementOptional.get();

    return new EwpIncomingMobilitiesApiConfiguration(
        apiElement.getGetUrl(),
        EwpApiUtils.getSupportedClientAuthenticationMethods(apiElement.getHttpSecurity()),
        EwpApiUtils.getSupportedServerAuthenticationMethods(apiElement.getHttpSecurity()),
        apiElement.getMaxOmobilityIds(),
        apiElement.getSendsNotifications() != null);
  }
}
