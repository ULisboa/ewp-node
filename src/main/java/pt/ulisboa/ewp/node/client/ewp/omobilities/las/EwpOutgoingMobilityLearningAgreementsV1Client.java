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
import pt.ulisboa.ewp.node.client.ewp.EwpApiClient;
import pt.ulisboa.ewp.node.client.ewp.EwpClient;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestSerializableBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilityLearningAgreementsApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.EwpApiVersionSpecification;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.OutgoingMobilityLearningAgreements;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpOutgoingMobilityLearningAgreementsV1Client
    extends EwpApiClient<EwpOutgoingMobilityLearningAgreementsApiConfiguration> {

  public EwpOutgoingMobilityLearningAgreementsV1Client(RegistryClient registryClient,
      EwpClient ewpClient) {
    super(registryClient, ewpClient);
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

    EwpRequest request = EwpRequest.createPost(api, api.getIndexUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpClient.executeAndLog(request, OmobilityLasIndexResponseV1.class);
  }

  public EwpSuccessOperationResult<OmobilityLasGetResponseV1> findBySendingHeiIdAndOutgoingMobilityIds(
      String sendingHeiId, Collection<String> outgoingMobilityIds) throws EwpClientErrorException {
    EwpOutgoingMobilityLearningAgreementsApiConfiguration api = getApiConfigurationForHeiId(
        sendingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    EwpRequest request = EwpRequest.createPost(api, api.getGetUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpClient.executeAndLog(request, OmobilityLasGetResponseV1.class);
  }

  public EwpSuccessOperationResult<OmobilityLasUpdateResponseV1> updateOutgoingMobilityLearningAgreement(
      OmobilityLasUpdateRequestV1 updateData) throws EwpClientErrorException {
    EwpOutgoingMobilityLearningAgreementsApiConfiguration api = getApiConfigurationForHeiId(
        updateData.getSendingHeiId());

    EwpRequest request = EwpRequest.createPost(api, api.getUpdateUrl(),
        new EwpRequestSerializableBody(updateData));
    return ewpClient.executeAndLog(request, OmobilityLasUpdateResponseV1.class);
  }

  @Override
  public EwpApiVersionSpecification<?, EwpOutgoingMobilityLearningAgreementsApiConfiguration>
  getApiVersionSpecification() {
    return OutgoingMobilityLearningAgreements.V1;
  }
}
