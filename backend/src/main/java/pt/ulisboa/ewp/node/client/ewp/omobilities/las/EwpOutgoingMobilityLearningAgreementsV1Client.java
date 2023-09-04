package pt.ulisboa.ewp.node.client.ewp.omobilities.las;

import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasGetResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasIndexResponseV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateRequestV1;
import eu.erasmuswithoutpaper.api.omobilities.las.v1.endpoints.OmobilityLasUpdateResponseV1;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.las.ForwardEwpApiOutgoingMobilityLearningAgreementsApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestSerializableBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilityLearningAgreementsApiConfiguration;
import pt.ulisboa.ewp.node.service.ewp.mapping.cache.EwpMobilityMappingCacheService;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.OutgoingMobilityLearningAgreements;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpOutgoingMobilityLearningAgreementsV1Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;
  private final EwpMobilityMappingCacheService mobilityMappingCacheService;

  public EwpOutgoingMobilityLearningAgreementsV1Client(RegistryClient registryClient,
                                                       EwpHttpClient ewpHttpClient, EwpMobilityMappingCacheService mobilityMappingCacheService) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
    this.mobilityMappingCacheService = mobilityMappingCacheService;
  }

  public ForwardEwpApiOutgoingMobilityLearningAgreementsApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpOutgoingMobilityLearningAgreementsApiConfiguration apiConfiguration = getApiConfigurationForHeiId(
        heiId);
    return new ForwardEwpApiOutgoingMobilityLearningAgreementsApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<OmobilityLasIndexResponseV1> findOutgoingMobilityIdsWithLearningAgreement(
      String sendingHeiId,
      List<String> receivingHeiIds,
      String receivingAcademicYearId,
      String globalId,
      String mobilityType,
      ZonedDateTime modifiedSince)
      throws EwpClientErrorException {
    EwpOutgoingMobilityLearningAgreementsApiConfiguration api = getApiConfigurationForHeiId(
        sendingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    bodyParams.param(EwpApiParamConstants.RECEIVING_HEI_ID, receivingHeiIds);
    bodyParams.param(EwpApiParamConstants.RECEIVING_ACADEMIC_YEAR_ID, receivingAcademicYearId);
    bodyParams.param(EwpApiParamConstants.GLOBAL_ID, globalId);
    bodyParams.param(EwpApiParamConstants.MOBILITY_TYPE, mobilityType);
    bodyParams.param(EwpApiParamConstants.MODIFIED_SINCE, modifiedSince);

    EwpRequest request = EwpRequest.createPost(api, "index", api.getIndexUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(request, OmobilityLasIndexResponseV1.class);
  }

  public EwpSuccessOperationResult<OmobilityLasGetResponseV1> findBySendingHeiIdAndOutgoingMobilityIds(
      String sendingHeiId, Collection<String> outgoingMobilityIds) throws EwpClientErrorException {
    EwpOutgoingMobilityLearningAgreementsApiConfiguration api = getApiConfigurationForHeiId(
        sendingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    EwpRequest request = EwpRequest.createPost(api, "get", api.getGetUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    EwpSuccessOperationResult<OmobilityLasGetResponseV1> result = ewpHttpClient.execute(request, OmobilityLasGetResponseV1.class);

    this.mobilityMappingCacheService.cacheMappingsFrom(result.getResponseBody());

    return result;
  }

  public EwpSuccessOperationResult<OmobilityLasUpdateResponseV1> updateOutgoingMobilityLearningAgreement(
      OmobilityLasUpdateRequestV1 updateData) throws EwpClientErrorException {
    EwpOutgoingMobilityLearningAgreementsApiConfiguration api = getApiConfigurationForHeiId(
        updateData.getSendingHeiId());

    EwpRequest request = EwpRequest.createPost(api, "update", api.getUpdateUrl(),
        new EwpRequestSerializableBody(updateData));
    return ewpHttpClient.execute(request, OmobilityLasUpdateResponseV1.class);
  }

  protected EwpOutgoingMobilityLearningAgreementsApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return OutgoingMobilityLearningAgreements.V1.getConfigurationForHeiId(registryClient, heiId);
  }
}
