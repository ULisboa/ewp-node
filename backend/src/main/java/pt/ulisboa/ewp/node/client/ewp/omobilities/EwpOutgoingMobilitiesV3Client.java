package pt.ulisboa.ewp.node.client.ewp.omobilities;

import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesGetResponseV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesIndexResponseV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesUpdateRequestV3;
import eu.erasmuswithoutpaper.api.omobilities.v3.endpoints.OmobilitiesUpdateResponseV3;
import java.time.ZonedDateTime;
import java.util.Collection;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.ForwardEwpApiOutgoingMobilitiesApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient.ResponseBodySpecification;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestSerializableBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilitiesApiConfiguration;
import pt.ulisboa.ewp.node.service.ewp.mapping.cache.EwpMobilityMappingCacheService;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.OutgoingMobilities;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpOutgoingMobilitiesV3Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  private final EwpMobilityMappingCacheService mobilityMappingCacheService;

  public EwpOutgoingMobilitiesV3Client(
      RegistryClient registryClient,
      EwpHttpClient ewpHttpClient,
      EwpMobilityMappingCacheService mobilityMappingCacheService) {
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

  public EwpSuccessOperationResult<OmobilitiesIndexResponseV3> findAllBySendingHeiId(
      String sendingHeiId,
      String receivingHeiId,
      String receivingAcademicYearId,
      ZonedDateTime modifiedSince,
      String globalId,
      String activityAttributes)
      throws EwpClientErrorException {
    EwpOutgoingMobilitiesApiConfiguration api = getApiConfigurationForHeiId(sendingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, receivingAcademicYearId);
    bodyParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);
    bodyParams.param(EwpApiParamConstants.GLOBAL_ID, globalId);
    bodyParams.param(EwpApiParamConstants.ACTIVITY_ATTRIBUTES, activityAttributes);

    EwpRequest request =
        EwpRequest.createPost(
            api, "index", api.getIndexUrl(), new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(
        request, ResponseBodySpecification.createStrict(OmobilitiesIndexResponseV3.class));
  }

  public EwpSuccessOperationResult<OmobilitiesGetResponseV3> findBySendingHeiIdAndOmobilityIds(
      String sendingHeiId, Collection<String> omobilityIds) throws EwpClientErrorException {
    EwpOutgoingMobilitiesApiConfiguration api = getApiConfigurationForHeiId(sendingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, omobilityIds);

    EwpRequest request =
        EwpRequest.createPost(
            api, "get", api.getGetUrl(), new EwpRequestFormDataUrlEncodedBody(bodyParams));
    EwpSuccessOperationResult<OmobilitiesGetResponseV3> result =
        ewpHttpClient.execute(
            request, ResponseBodySpecification.createStrict(OmobilitiesGetResponseV3.class));

    this.mobilityMappingCacheService.cacheMappingsFrom(result.getResponseBody());

    return result;
  }

  public EwpSuccessOperationResult<OmobilitiesUpdateResponseV3> updateOmobility(
      String sendingHeiId, OmobilitiesUpdateRequestV3 updateData) throws EwpClientErrorException {
    EwpOutgoingMobilitiesApiConfiguration api = getApiConfigurationForHeiId(sendingHeiId);

    EwpRequest request =
        EwpRequest.createPost(
            api, "update", api.getUpdateUrl(), new EwpRequestSerializableBody(updateData));

    return ewpHttpClient.execute(
        request, ResponseBodySpecification.createStrict(OmobilitiesUpdateResponseV3.class));
  }

  protected EwpOutgoingMobilitiesApiConfiguration getApiConfigurationForHeiId(String heiId) {
    return OutgoingMobilities.V3.getConfigurationForHeiId(registryClient, heiId);
  }
}
