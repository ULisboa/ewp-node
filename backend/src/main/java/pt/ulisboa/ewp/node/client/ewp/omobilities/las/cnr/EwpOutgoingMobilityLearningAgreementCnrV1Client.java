package pt.ulisboa.ewp.node.client.ewp.omobilities.las.cnr;

import eu.erasmuswithoutpaper.api.omobilities.las.cnr.v1.OmobilityLaCnrResponseV1;
import java.util.List;
import org.springframework.stereotype.Service;
import pt.ulisboa.ewp.node.api.ewp.utils.EwpApiParamConstants;
import pt.ulisboa.ewp.node.api.host.forward.ewp.dto.omobilities.las.cnr.ForwardEwpApiOutgoingMobilityLearningAgreementCnrApiSpecificationResponseDTO;
import pt.ulisboa.ewp.node.client.ewp.exception.EwpClientErrorException;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient;
import pt.ulisboa.ewp.node.client.ewp.http.EwpHttpClient.ResponseBodySpecification;
import pt.ulisboa.ewp.node.client.ewp.operation.request.EwpRequest;
import pt.ulisboa.ewp.node.client.ewp.operation.request.body.EwpRequestFormDataUrlEncodedBody;
import pt.ulisboa.ewp.node.client.ewp.operation.result.EwpSuccessOperationResult;
import pt.ulisboa.ewp.node.client.ewp.registry.RegistryClient;
import pt.ulisboa.ewp.node.domain.entity.api.ewp.EwpOutgoingMobilityLearningAgreementCnrApiConfiguration;
import pt.ulisboa.ewp.node.utils.EwpApiSpecification.OutgoingMobilityLearningAgreementCnr;
import pt.ulisboa.ewp.node.utils.http.HttpParams;

@Service
public class EwpOutgoingMobilityLearningAgreementCnrV1Client {

  private final RegistryClient registryClient;
  private final EwpHttpClient ewpHttpClient;

  public EwpOutgoingMobilityLearningAgreementCnrV1Client(RegistryClient registryClient,
      EwpHttpClient ewpHttpClient) {
    this.registryClient = registryClient;
    this.ewpHttpClient = ewpHttpClient;
  }

  public ForwardEwpApiOutgoingMobilityLearningAgreementCnrApiSpecificationResponseDTO getApiSpecification(
      String heiId) {
    EwpOutgoingMobilityLearningAgreementCnrApiConfiguration apiConfiguration = getApiConfigurationForHeiId(
        heiId);
    return new ForwardEwpApiOutgoingMobilityLearningAgreementCnrApiSpecificationResponseDTO(
        apiConfiguration.getMaxOmobilityIds().intValueExact());
  }

  public EwpSuccessOperationResult<OmobilityLaCnrResponseV1> sendChangeNotification(
      String sendingHeiId, String receivingHeiId, List<String> outgoingMobilityIds)
      throws EwpClientErrorException {
    EwpOutgoingMobilityLearningAgreementCnrApiConfiguration api = getApiConfigurationForHeiId(
        receivingHeiId);

    HttpParams bodyParams = new HttpParams();
    bodyParams.param(EwpApiParamConstants.SENDING_HEI_ID, sendingHeiId);
    bodyParams.param(EwpApiParamConstants.OMOBILITY_ID, outgoingMobilityIds);

    EwpRequest request = EwpRequest.createPost(api, "", api.getUrl(),
        new EwpRequestFormDataUrlEncodedBody(bodyParams));
    return ewpHttpClient.execute(
        request, ResponseBodySpecification.createWithOptionalType(OmobilityLaCnrResponseV1.class));
  }

  protected EwpOutgoingMobilityLearningAgreementCnrApiConfiguration getApiConfigurationForHeiId(
      String heiId) {
    return OutgoingMobilityLearningAgreementCnr.V1.getConfigurationForHeiId(registryClient, heiId);
  }
}
