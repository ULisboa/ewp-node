package pt.ulisboa.ewp.node.client.ewp.omobilities;

import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.OmobilitiesGetResponseV2;
import eu.erasmuswithoutpaper.api.omobilities.v2.endpoints.OmobilitiesIndexResponseV2;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilitiesApiConfiguration;
import pt.ulisboa.ewp.node.service.ewp.mapping.cache.EwpMobilityMappingCacheService;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.OutgoingMobilities;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpOutgoingMobilitiesV2Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  private final EwpMobilityMappingCacheService mobilityMappingCacheService;

  public EwpOutgoingMobilitiesV2Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient, EwpMobilityMappingCacheService mobilityMappingCacheService) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
    this.mobilityMappingCacheService = mobilityMappingCacheService;
  }

  public ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpOutgoingMobilitiesApiConfiguration apiConfiguration = getApiConfigurationForHeiId(heiId);
    return new ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<OmobilitiesIndexResponseV2> findAllBySendingHeiId(
      String sendingHeiId,
      List<String> receivingHeiIds,
      String receivingAcademicYearId,
      ZonedDateTime modifiedSince)
      throws EwpClientErrorException {
    EwpOutgoingMobilitiesApiConfiguration api = getApiConfigurationForHeiId(sendingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiIds);
    bodyParams.param(EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, receivingAcademicYearId);
    bodyParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);

    EwpRequest request = EwpRequest.createPost(api, api.getIndexUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(request, OmobilitiesIndexResponseV2.class);
  }

  public EwpSuccessOperationResult<OmobilitiesGetResponseV2> findBySendingHeiIdAndOmobilityIds(
      String sendingHeiId, Collection<String> omobilityIds) throws EwpClientErrorException {
    EwpOutgoingMobilitiesApiConfiguration api = getApiConfigurationForHeiId(sendingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    EwpRequest request = EwpRequest.createPost(api, api.getGetUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    EwpSuccessOperationResult<OmobilitiesGetResponseV2> result = ewpHttpClient.execute(
        request, OmobilitiesGetResponseV2.class);

    this.mobilityMappingCacheService.cacheMappingsFrom(result.getResponseBody());

    return result;
  }

  protected EwpOutgoingMobilitiesApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return OutgoingMobilities.V2.getConfigurationForHeiId(registryClient, heiId);
  }
}
